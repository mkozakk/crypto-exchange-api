package com.cryptoexchange.web;

import com.cryptoexchange.dto.TransactionResponse;
import com.cryptoexchange.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Transactions")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Operation(summary = "Recent trade history, optionally filtered by symbol")
    @GetMapping
    public List<TransactionResponse> history(@RequestParam(required = false) String symbol) {
        List<com.cryptoexchange.model.Transaction> transactions = symbol == null
                ? transactionRepository.findTop50ByOrderByExecutedAtDesc()
                : transactionRepository.findBySymbolOrderByExecutedAtDesc(symbol);
        return transactions.stream().map(TransactionResponse::from).toList();
    }
}
