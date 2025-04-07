package com.cryptoexchange.websocket;

import com.cryptoexchange.dto.PriceTick;
import com.cryptoexchange.event.OrderBookChangedEvent;
import com.cryptoexchange.event.PriceUpdatedEvent;
import com.cryptoexchange.service.OrderBookService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MarketSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderBookService orderBookService;

    public MarketSocketPublisher(SimpMessagingTemplate messagingTemplate,
                                 OrderBookService orderBookService) {
        this.messagingTemplate = messagingTemplate;
        this.orderBookService = orderBookService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPriceUpdated(PriceUpdatedEvent event) {
        messagingTemplate.convertAndSend("/topic/price",
                new PriceTick(event.symbol(), event.price()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderBookChanged(OrderBookChangedEvent event) {
        messagingTemplate.convertAndSend("/topic/orderbook/" + event.symbol(),
                orderBookService.snapshot(event.symbol()));
    }
}
