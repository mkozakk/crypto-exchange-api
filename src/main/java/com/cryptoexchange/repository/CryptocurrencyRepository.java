package com.cryptoexchange.repository;

import com.cryptoexchange.model.Cryptocurrency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, Long> {

    Optional<Cryptocurrency> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
