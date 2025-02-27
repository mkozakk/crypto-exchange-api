package com.cryptoexchange.service;

import com.cryptoexchange.event.PriceUpdatedEvent;
import com.cryptoexchange.exception.NotFoundException;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.repository.CryptocurrencyRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CryptocurrencyService {

    private final CryptocurrencyRepository repository;
    private final ApplicationEventPublisher events;

    public CryptocurrencyService(CryptocurrencyRepository repository,
                                 ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional(readOnly = true)
    public List<Cryptocurrency> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cryptocurrency getBySymbol(String symbol) {
        return repository.findBySymbol(symbol)
                .orElseThrow(() -> new NotFoundException("Unknown cryptocurrency: " + symbol));
    }

    @Transactional
    public void updatePrice(String symbol, BigDecimal newPrice) {
        Cryptocurrency crypto = getBySymbol(symbol);
        crypto.setCurrentPrice(newPrice);
        events.publishEvent(new PriceUpdatedEvent(symbol, newPrice));
    }
}
