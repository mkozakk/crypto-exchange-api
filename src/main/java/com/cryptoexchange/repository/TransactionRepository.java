package com.cryptoexchange.repository;

import com.cryptoexchange.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop50ByOrderByExecutedAtDesc();

    List<Transaction> findBySymbolOrderByExecutedAtDesc(String symbol);
}
