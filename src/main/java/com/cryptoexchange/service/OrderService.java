package com.cryptoexchange.service;

import com.cryptoexchange.dto.PlaceOrderRequest;
import com.cryptoexchange.exception.InvalidOrderException;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.model.OrderType;
import com.cryptoexchange.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CryptocurrencyService cryptocurrencyService;
    private final BalanceService balanceService;

    public OrderService(OrderRepository orderRepository,
                        CryptocurrencyService cryptocurrencyService,
                        BalanceService balanceService) {
        this.orderRepository = orderRepository;
        this.cryptocurrencyService = cryptocurrencyService;
        this.balanceService = balanceService;
    }

    @Transactional
    public Order placeOrder(PlaceOrderRequest request, OrderSource source) {
        Cryptocurrency crypto = cryptocurrencyService.getBySymbol(request.getSymbol());
        validate(request);

        if (source == OrderSource.USER) {
            checkFunds(request, crypto);
        }

        BigDecimal price = request.getType() == OrderType.LIMIT ? request.getPrice() : null;
        Order order = new Order(crypto.getSymbol(), request.getSide(), request.getType(),
                price, request.getQuantity(), source);
        return orderRepository.save(order);
    }

    private void validate(PlaceOrderRequest request) {
        if (request.getType() == OrderType.LIMIT
                && (request.getPrice() == null || request.getPrice().signum() <= 0)) {
            throw new InvalidOrderException("Limit orders require a positive price");
        }
        if (request.getType() == OrderType.MARKET && request.getPrice() != null) {
            throw new InvalidOrderException("Market orders must not specify a price");
        }
    }

    private void checkFunds(PlaceOrderRequest request, Cryptocurrency crypto) {
        if (request.getSide() == OrderSide.BUY) {
            BigDecimal unitPrice = request.getPrice();
            balanceService.requireCash(unitPrice.multiply(request.getQuantity()));
        }
    }
}
