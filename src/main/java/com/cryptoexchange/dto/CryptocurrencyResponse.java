package com.cryptoexchange.dto;

import com.cryptoexchange.model.Cryptocurrency;

import java.math.BigDecimal;

public record CryptocurrencyResponse(String symbol, String name, BigDecimal currentPrice) {

    public static CryptocurrencyResponse from(Cryptocurrency crypto) {
        return new CryptocurrencyResponse(crypto.getSymbol(), crypto.getName(), crypto.getCurrentPrice());
    }
}
