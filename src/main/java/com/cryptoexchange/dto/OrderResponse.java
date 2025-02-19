package com.cryptoexchange.dto;

import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.model.OrderStatus;
import com.cryptoexchange.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String symbol,
        OrderSide side,
        OrderType type,
        BigDecimal price,
        BigDecimal quantity,
        BigDecimal remainingQuantity,
        BigDecimal filled,
        OrderStatus status,
        OrderSource source,
        Instant createdAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSymbol(),
                order.getSide(),
                order.getType(),
                order.getPrice(),
                order.getQuantity(),
                order.getRemainingQuantity(),
                order.getFilledQuantity(),
                order.getStatus(),
                order.getSource(),
                order.getCreatedAt());
    }
}
