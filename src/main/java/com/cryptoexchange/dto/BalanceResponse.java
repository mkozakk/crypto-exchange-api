package com.cryptoexchange.dto;

import java.math.BigDecimal;
import java.util.List;

public record BalanceResponse(BigDecimal cash, List<CryptoHolding> holdings) {
}
