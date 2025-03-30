package com.cryptoexchange.web;

import com.cryptoexchange.dto.OrderBookResponse;
import com.cryptoexchange.service.CryptocurrencyService;
import com.cryptoexchange.service.OrderBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order book")
@RestController
@RequestMapping("/api/orderbook")
public class OrderBookController {

    private final OrderBookService orderBookService;
    private final CryptocurrencyService cryptocurrencyService;

    public OrderBookController(OrderBookService orderBookService,
                              CryptocurrencyService cryptocurrencyService) {
        this.orderBookService = orderBookService;
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @Operation(summary = "Get the bids and asks for a symbol")
    @GetMapping("/{symbol}")
    public OrderBookResponse get(@PathVariable String symbol) {
        cryptocurrencyService.getBySymbol(symbol);
        return orderBookService.snapshot(symbol);
    }
}
