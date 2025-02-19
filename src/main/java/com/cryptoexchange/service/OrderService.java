package com.cryptoexchange.service;

import com.cryptoexchange.dto.PlaceOrderRequest;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CryptocurrencyService cryptocurrencyService;

    public OrderService(OrderRepository orderRepository,
                        CryptocurrencyService cryptocurrencyService) {
        this.orderRepository = orderRepository;
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @Transactional
    public Order placeOrder(PlaceOrderRequest request, OrderSource source) {
        Cryptocurrency crypto = cryptocurrencyService.getBySymbol(request.getSymbol());
        Order order = new Order(crypto.getSymbol(), request.getSide(), request.getType(),
                request.getPrice(), request.getQuantity(), source);
        return orderRepository.save(order);
    }
}
