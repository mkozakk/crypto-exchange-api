package com.cryptoexchange.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "cash_balance")
public class CashBalance {

    @Id
    private Long id;

    @Column(nullable = false, precision = 30, scale = 8)
    private BigDecimal amount;

    protected CashBalance() {
    }

    public CashBalance(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
