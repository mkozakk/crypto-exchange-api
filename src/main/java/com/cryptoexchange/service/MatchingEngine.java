package com.cryptoexchange.service;

import com.cryptoexchange.event.OrderBookChangedEvent;
import com.cryptoexchange.exception.InsufficientLiquidityException;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.model.OrderStatus;
import com.cryptoexchange.model.OrderType;
import com.cryptoexchange.model.Transaction;
import com.cryptoexchange.repository.OrderRepository;
import com.cryptoexchange.repository.TransactionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MatchingEngine {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final OrderBookService orderBookService;
    private final BalanceService balanceService;
    private final CryptocurrencyService cryptocurrencyService;
    private final ApplicationEventPublisher events;

    public MatchingEngine(OrderRepository orderRepository,
                          TransactionRepository transactionRepository,
                          OrderBookService orderBookService,
                          BalanceService balanceService,
                          CryptocurrencyService cryptocurrencyService,
                          ApplicationEventPublisher events) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.orderBookService = orderBookService;
        this.balanceService = balanceService;
        this.cryptocurrencyService = cryptocurrencyService;
        this.events = events;
    }

    @Transactional
    public void match(Order incoming) {
        List<Order> restingOrders = incoming.getSide() == OrderSide.BUY
                ? orderBookService.activeAsks(incoming.getSymbol())
                : orderBookService.activeBids(incoming.getSymbol());

        if (incoming.getType() == OrderType.MARKET) {
            requireLiquidity(incoming, restingOrders);
        }

        for (Order resting : restingOrders) {
            if (incoming.getRemainingQuantity().signum() == 0) {
                break;
            }
            if (!crosses(incoming, resting)) {
                break;
            }
            executeTrade(incoming, resting);
        }

        finalizeStatus(incoming);
        orderRepository.save(incoming);
        events.publishEvent(new OrderBookChangedEvent(incoming.getSymbol()));
    }

    private void requireLiquidity(Order incoming, List<Order> restingOrders) {
        BigDecimal available = restingOrders.stream()
                .map(Order::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (available.compareTo(incoming.getQuantity()) < 0) {
            throw new InsufficientLiquidityException(
                    "Not enough liquidity to fill market order for " + incoming.getSymbol());
        }
    }

    private boolean crosses(Order incoming, Order resting) {
        if (incoming.getType() == OrderType.MARKET) {
            return true;
        }
        if (incoming.getSide() == OrderSide.BUY) {
            return resting.getPrice().compareTo(incoming.getPrice()) <= 0;
        }
        return resting.getPrice().compareTo(incoming.getPrice()) < 0;
    }

    private void executeTrade(Order incoming, Order resting) {
        BigDecimal tradeQty = incoming.getRemainingQuantity().min(resting.getRemainingQuantity());
        BigDecimal tradePrice = resting.getPrice();
        BigDecimal value = tradePrice.multiply(tradeQty);

        incoming.setRemainingQuantity(incoming.getRemainingQuantity().subtract(tradeQty));
        resting.setRemainingQuantity(resting.getRemainingQuantity().subtract(tradeQty));
        resting.setStatus(resting.getRemainingQuantity().signum() == 0
                ? OrderStatus.FILLED : OrderStatus.PARTIAL);
        orderRepository.save(resting);

        Order buyOrder = incoming.getSide() == OrderSide.BUY ? incoming : resting;
        Order sellOrder = incoming.getSide() == OrderSide.BUY ? resting : incoming;

        transactionRepository.save(new Transaction(
                incoming.getSymbol(), buyOrder.getId(), sellOrder.getId(), tradePrice, tradeQty));

        settle(buyOrder, sellOrder, value, tradeQty);
        cryptocurrencyService.updatePrice(incoming.getSymbol(), tradePrice);
    }

    private void settle(Order buyOrder, Order sellOrder, BigDecimal value, BigDecimal tradeQty) {
        if (buyOrder.getSource() == OrderSource.USER) {
            balanceService.settleBuy(buyOrder.getSymbol(), value, tradeQty);
        }
        if (sellOrder.getSource() == OrderSource.USER) {
            balanceService.settleSell(sellOrder.getSymbol(), value, tradeQty);
        }
    }

    private void finalizeStatus(Order incoming) {
        if (incoming.getRemainingQuantity().signum() == 0) {
            incoming.setStatus(OrderStatus.FILLED);
        } else if (incoming.getFilledQuantity().signum() > 0) {
            incoming.setStatus(OrderStatus.PARTIAL);
        } else {
            incoming.setStatus(OrderStatus.OPEN);
        }
    }
}
