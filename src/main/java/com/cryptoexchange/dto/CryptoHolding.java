package com.cryptoexchange.dto;

import java.math.BigDecimal;

public record CryptoHolding(String symbol, BigDecimal quantity) {
}
