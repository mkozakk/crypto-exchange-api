package com.cryptoexchange.dto;

import java.util.List;

public record OrderBookResponse(String symbol, List<OrderBookLevel> bids, List<OrderBookLevel> asks) {
}
