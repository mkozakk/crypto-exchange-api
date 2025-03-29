package com.cryptoexchange.web;

import com.cryptoexchange.dto.OrderResponse;
import com.cryptoexchange.dto.PlaceOrderRequest;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Place a limit or market order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request, OrderSource.USER);
        return OrderResponse.from(order);
    }

    @Operation(summary = "Cancel an open order")
    @DeleteMapping("/{id}")
    public OrderResponse cancel(@PathVariable Long id) {
        return OrderResponse.from(orderService.cancelOrder(id));
    }
}
