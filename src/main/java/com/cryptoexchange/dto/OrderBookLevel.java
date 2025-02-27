package com.cryptoexchange.dto;

import java.math.BigDecimal;

public record OrderBookLevel(BigDecimal price, BigDecimal quantity) {
}
