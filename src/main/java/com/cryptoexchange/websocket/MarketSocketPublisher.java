package com.cryptoexchange.websocket;

import com.cryptoexchange.dto.PriceTick;
import com.cryptoexchange.event.OrderBookChangedEvent;
import com.cryptoexchange.event.PriceUpdatedEvent;
import com.cryptoexchange.service.OrderBookService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarketSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderBookService orderBookService;

    public MarketSocketPublisher(SimpMessagingTemplate messagingTemplate,
                                 OrderBookService orderBookService) {
        this.messagingTemplate = messagingTemplate;
        this.orderBookService = orderBookService;
    }

    @EventListener
    public void onPriceUpdated(PriceUpdatedEvent event) {
        messagingTemplate.convertAndSend("/topic/price",
                new PriceTick(event.symbol(), event.price()));
    }

    @EventListener
    public void onOrderBookChanged(OrderBookChangedEvent event) {
        messagingTemplate.convertAndSend("/topic/orderbook/" + event.symbol(),
                orderBookService.snapshot(event.symbol()));
    }
}
