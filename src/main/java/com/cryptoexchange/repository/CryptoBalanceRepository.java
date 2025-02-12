package com.cryptoexchange.repository;

import com.cryptoexchange.model.CryptoBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoBalanceRepository extends JpaRepository<CryptoBalance, Long> {

    Optional<CryptoBalance> findBySymbol(String symbol);
}
