package com.cryptoexchange.web;

import com.cryptoexchange.dto.BalanceResponse;
import com.cryptoexchange.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Balance")
@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Operation(summary = "Current cash and crypto balances")
    @GetMapping
    public BalanceResponse balance() {
        return new BalanceResponse(balanceService.getCash(), balanceService.getHoldings());
    }
}
