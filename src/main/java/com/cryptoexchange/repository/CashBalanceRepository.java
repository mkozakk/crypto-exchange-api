package com.cryptoexchange.repository;

import com.cryptoexchange.model.CashBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashBalanceRepository extends JpaRepository<CashBalance, Long> {

    Long WALLET_ID = 1L;
}
