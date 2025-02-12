package com.cryptoexchange.repository;

import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findBySymbolAndSideAndStatusInOrderByPriceAscCreatedAtAsc(
            String symbol, OrderSide side, List<OrderStatus> statuses);

    List<Order> findBySymbolAndSideAndStatusInOrderByPriceDescCreatedAtAsc(
            String symbol, OrderSide side, List<OrderStatus> statuses);
}
