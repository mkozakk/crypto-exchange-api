package com.cryptoexchange.dto;

import java.math.BigDecimal;

public record PriceTick(String symbol, BigDecimal price) {
}
