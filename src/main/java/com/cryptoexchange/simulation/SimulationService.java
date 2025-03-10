package com.cryptoexchange.simulation;

import com.cryptoexchange.dto.PlaceOrderRequest;
import com.cryptoexchange.exception.InsufficientLiquidityException;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.model.Order;
import com.cryptoexchange.model.OrderSide;
import com.cryptoexchange.model.OrderType;
import com.cryptoexchange.model.OrderSource;
import com.cryptoexchange.service.CryptocurrencyService;
import com.cryptoexchange.service.OrderBookService;
import com.cryptoexchange.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private static final double VOLATILITY = 0.002;
    private static final double IMBALANCE_BIAS = 0.0015;
    private static final double SPREAD = 0.01;
    private static final int ORDERS_PER_TICK = 3;

    private final CryptocurrencyService cryptocurrencyService;
    private final OrderBookService orderBookService;
    private final OrderService orderService;
    private final Random random = new Random();

    @Value("${simulation.enabled:true}")
    private boolean enabled;

    public SimulationService(CryptocurrencyService cryptocurrencyService,
                             OrderBookService orderBookService,
                             OrderService orderService) {
        this.cryptocurrencyService = cryptocurrencyService;
        this.orderBookService = orderBookService;
        this.orderService = orderService;
    }

    @Scheduled(fixedRateString = "${simulation.tick-ms:500}")
    public void tick() {
        if (!enabled) {
            return;
        }
        for (Cryptocurrency coin : cryptocurrencyService.findAll()) {
            BigDecimal reference = movePrice(coin);
            generateOrders(coin.getSymbol(), reference);
        }
    }

    private BigDecimal movePrice(Cryptocurrency coin) {
        double drift = random.nextGaussian() * VOLATILITY + imbalance(coin.getSymbol()) * IMBALANCE_BIAS;
        BigDecimal factor = BigDecimal.valueOf(1 + drift);
        BigDecimal newPrice = coin.getCurrentPrice().multiply(factor).setScale(8, RoundingMode.HALF_UP);
        if (newPrice.signum() <= 0) {
            newPrice = coin.getCurrentPrice();
        }
        cryptocurrencyService.updatePrice(coin.getSymbol(), newPrice);
        return newPrice;
    }

    private double imbalance(String symbol) {
        BigDecimal bidQty = totalQuantity(orderBookService.activeBids(symbol));
        BigDecimal askQty = totalQuantity(orderBookService.activeAsks(symbol));
        BigDecimal total = bidQty.add(askQty);
        if (total.signum() == 0) {
            return 0;
        }
        return bidQty.subtract(askQty).divide(total, 8, RoundingMode.HALF_UP).doubleValue();
    }

    private BigDecimal totalQuantity(List<Order> orders) {
        return orders.stream().map(Order::getRemainingQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void generateOrders(String symbol, BigDecimal reference) {
        for (int i = 0; i < ORDERS_PER_TICK; i++) {
            PlaceOrderRequest request = new PlaceOrderRequest();
            request.setSymbol(symbol);
            request.setSide(random.nextBoolean() ? OrderSide.BUY : OrderSide.SELL);
            request.setQuantity(randomQuantity(reference));

            if (random.nextInt(5) == 0) {
                request.setType(OrderType.MARKET);
            } else {
                request.setType(OrderType.LIMIT);
                request.setPrice(randomPrice(reference));
            }

            submit(request);
        }
    }

    private void submit(PlaceOrderRequest request) {
        try {
            orderService.placeOrder(request, OrderSource.SIMULATOR);
        } catch (InsufficientLiquidityException ignored) {
        } catch (RuntimeException ex) {
            log.debug("Skipping simulated order: {}", ex.getMessage());
        }
    }

    private BigDecimal randomPrice(BigDecimal reference) {
        double spread = (random.nextDouble() * 2 - 1) * SPREAD;
        return reference.multiply(BigDecimal.valueOf(1 + spread)).setScale(8, RoundingMode.HALF_UP);
    }

    private BigDecimal randomQuantity(BigDecimal reference) {
        double notional = 200 + random.nextDouble() * 1800;
        return BigDecimal.valueOf(notional)
                .divide(reference, 8, RoundingMode.HALF_UP)
                .max(new BigDecimal("0.00000001"));
    }
}
