package com.cryptoexchange.websocket;

import com.cryptoexchange.dto.PriceTick;
import com.cryptoexchange.event.PriceUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MarketSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public MarketSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void onPriceUpdated(PriceUpdatedEvent event) {
        messagingTemplate.convertAndSend("/topic/price",
                new PriceTick(event.symbol(), event.price()));
    }
}
