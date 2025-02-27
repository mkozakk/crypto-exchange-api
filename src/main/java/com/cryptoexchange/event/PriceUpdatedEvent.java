package com.cryptoexchange.event;

import java.math.BigDecimal;

public record PriceUpdatedEvent(String symbol, BigDecimal price) {
}
