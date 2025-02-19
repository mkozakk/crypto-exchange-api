package com.cryptoexchange.service;

import com.cryptoexchange.exception.NotFoundException;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.repository.CryptocurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CryptocurrencyService {

    private final CryptocurrencyRepository repository;

    public CryptocurrencyService(CryptocurrencyRepository repository) {
        this.repository = repository;
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
}
