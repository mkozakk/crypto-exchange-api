package com.cryptoexchange.dto;

import com.cryptoexchange.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        String symbol,
        BigDecimal price,
        BigDecimal quantity,
        Instant executedAt) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSymbol(),
                transaction.getPrice(),
                transaction.getQuantity(),
                transaction.getExecutedAt());
    }
}
