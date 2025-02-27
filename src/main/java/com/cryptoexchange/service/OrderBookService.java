package com.cryptoexchange.service;

import com.cryptoexchange.dto.OrderBookLevel;
import com.cryptoexchange.dto.OrderBookResponse;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderStatus;
import com.cryptoexchange.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderBookService {

    private static final List<OrderStatus> ACTIVE = List.of(OrderStatus.OPEN, OrderStatus.PARTIAL);

    private final OrderRepository orderRepository;

    public OrderBookService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> activeAsks(String symbol) {
        return orderRepository.findBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
                symbol, OrderSide.SELL, ACTIVE);
    }

    public List<Order> activeBids(String symbol) {
        return orderRepository.findBySymbolAndSideAndStatusInOrderByPriceDescCreatedAtAsc(
                symbol, OrderSide.BUY, ACTIVE);
    }

    @Transactional(readOnly = true)
    public OrderBookResponse snapshot(String symbol) {
        List<OrderBookLevel> bids = aggregate(activeBids(symbol), true);
        List<OrderBookLevel> asks = aggregate(activeAsks(symbol), false);
        return new OrderBookResponse(symbol, bids, asks);
    }

    private List<OrderBookLevel> aggregate(List<Order> orders, boolean descending) {
        Map<BigDecimal, BigDecimal> byPrice = new LinkedHashMap<>();
        for (Order order : orders) {
            byPrice.merge(order.getPrice(), order.getRemainingQuantity(), BigDecimal::add);
        }
        List<OrderBookLevel> levels = new ArrayList<>();
        byPrice.forEach((price, qty) -> levels.add(new OrderBookLevel(price, qty)));
        Comparator<OrderBookLevel> byPriceAsc = Comparator.comparing(OrderBookLevel::price);
        levels.sort(descending ? byPriceAsc.reversed() : byPriceAsc);
        return levels;
    }
}
