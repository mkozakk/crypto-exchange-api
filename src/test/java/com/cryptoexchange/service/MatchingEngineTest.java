package com.cryptoexchange.service;

import com.cryptoexchange.exception.InsufficientLiquidityException;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.model.OrderStatus;
import com.cryptoexchange.model.OrderType;
import com.cryptoexchange.repository.OrderRepository;
import com.cryptoexchange.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MatchingEngineTest {

    private OrderRepository orderRepository;
    private TransactionRepository transactionRepository;
    private OrderBookService orderBookService;
    private BalanceService balanceService;
    private CryptocurrencyService cryptocurrencyService;
    private MatchingEngine engine;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        orderBookService = mock(OrderBookService.class);
        balanceService = mock(BalanceService.class);
        cryptocurrencyService = mock(CryptocurrencyService.class);
        ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
        engine = new MatchingEngine(orderRepository, transactionRepository, orderBookService,
                balanceService, cryptocurrencyService, events);
    }

    @Test
    void limitBuyFullyMatchesRestingAskAndSettlesWallet() {
        Order ask = new Order("BTC", OrderSide.SELL, OrderType.LIMIT,
                new BigDecimal("100"), new BigDecimal("1"), OrderSource.SIMULATOR);
        when(orderBookService.activeAsks("BTC")).thenReturn(List.of(ask));

        Order buy = new Order("BTC", OrderSide.BUY, OrderType.LIMIT,
                new BigDecimal("100"), new BigDecimal("1"), OrderSource.USER);

        engine.match(buy);

        assertThat(buy.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(buy.getRemainingQuantity()).isEqualByComparingTo("0");
        verify(transactionRepository).save(any());
        verify(balanceService).settleBuy(eq("BTC"), eq(new BigDecimal("100")), eq(new BigDecimal("1")));
        verify(cryptocurrencyService).updatePrice("BTC", new BigDecimal("100"));
    }

    @Test
    void limitBuyBelowBestAskRestsInBook() {
        Order ask = new Order("BTC", OrderSide.SELL, OrderType.LIMIT,
                new BigDecimal("100"), new BigDecimal("1"), OrderSource.SIMULATOR);
        when(orderBookService.activeAsks("BTC")).thenReturn(List.of(ask));

        Order buy = new Order("BTC", OrderSide.BUY, OrderType.LIMIT,
                new BigDecimal("90"), new BigDecimal("1"), OrderSource.USER);

        engine.match(buy);

        assertThat(buy.getStatus()).isEqualTo(OrderStatus.OPEN);
        assertThat(buy.getRemainingQuantity()).isEqualByComparingTo("1");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void marketBuyWithoutLiquidityIsRejected() {
        when(orderBookService.activeAsks("BTC")).thenReturn(List.of());

        Order buy = new Order("BTC", OrderSide.BUY, OrderType.MARKET,
                null, new BigDecimal("1"), OrderSource.USER);

        assertThatThrownBy(() -> engine.match(buy))
                .isInstanceOf(InsufficientLiquidityException.class);
        verify(balanceService, never()).settleBuy(anyString(), any(), any());
    }
}
