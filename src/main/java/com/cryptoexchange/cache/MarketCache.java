package com.cryptoexchange.cache;

import com.cryptoexchange.dto.OrderBookResponse;
import com.cryptoexchange.event.OrderBookChangedEvent;
import com.cryptoexchange.event.PriceUpdatedEvent;
import com.cryptoexchange.service.OrderBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Component
public class MarketCache {

    private static final Logger log = LoggerFactory.getLogger(MarketCache.class);
    private static final String PRICES_KEY = "prices";
    private static final String ORDERBOOK_KEY = "orderbook:";
    private static final Duration ORDERBOOK_TTL = Duration.ofSeconds(10);

    private final StringRedisTemplate redis;
    private final OrderBookService orderBookService;
    private final ObjectMapper objectMapper;

    public MarketCache(StringRedisTemplate redis,
                       OrderBookService orderBookService,
                       ObjectMapper redisObjectMapper) {
        this.redis = redis;
        this.orderBookService = orderBookService;
        this.objectMapper = redisObjectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPriceUpdated(PriceUpdatedEvent event) {
        try {
            redis.opsForHash().put(PRICES_KEY, event.symbol(), event.price().toPlainString());
        } catch (RuntimeException ex) {
            log.debug("Could not cache price for {}: {}", event.symbol(), ex.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderBookChanged(OrderBookChangedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(orderBookService.snapshot(event.symbol()));
            redis.opsForValue().set(ORDERBOOK_KEY + event.symbol(), json, ORDERBOOK_TTL);
        } catch (Exception ex) {
            log.debug("Could not cache order book for {}: {}", event.symbol(), ex.getMessage());
        }
    }

    public Optional<BigDecimal> cachedPrice(String symbol) {
        try {
            Object value = redis.opsForHash().get(PRICES_KEY, symbol);
            return Optional.ofNullable(value).map(v -> new BigDecimal(v.toString()));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    public Optional<OrderBookResponse> cachedOrderBook(String symbol) {
        try {
            String json = redis.opsForValue().get(ORDERBOOK_KEY + symbol);
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, OrderBookResponse.class));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
