package com.cryptoexchange.web;

import com.cryptoexchange.dto.BalanceResponse;
import com.cryptoexchange.dto.CashRequest;
import com.cryptoexchange.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cash")
@RestController
@RequestMapping("/api/cash")
public class CashController {

    private final BalanceService balanceService;

    public CashController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Operation(summary = "Deposit cash into the demo wallet")
    @PostMapping("/deposit")
    public BalanceResponse deposit(@Valid @RequestBody CashRequest request) {
        balanceService.depositCash(request.getAmount());
        return new BalanceResponse(balanceService.getCash(), balanceService.getHoldings());
    }

    @Operation(summary = "Withdraw cash from the demo wallet")
    @PostMapping("/withdraw")
    public BalanceResponse withdraw(@Valid @RequestBody CashRequest request) {
        balanceService.withdrawCash(request.getAmount());
        return new BalanceResponse(balanceService.getCash(), balanceService.getHoldings());
    }
}
