package com.cryptoexchange.web;

import com.cryptoexchange.dto.CryptocurrencyResponse;
import com.cryptoexchange.service.CryptocurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Cryptocurrencies")
@RestController
@RequestMapping("/api/cryptocurrencies")
public class CryptocurrencyController {

    private final CryptocurrencyService cryptocurrencyService;

    public CryptocurrencyController(CryptocurrencyService cryptocurrencyService) {
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @Operation(summary = "List all cryptocurrencies with their current prices")
    @GetMapping
    public List<CryptocurrencyResponse> list() {
        return cryptocurrencyService.findAll().stream()
                .map(CryptocurrencyResponse::from)
                .toList();
    }
}
