package com.cryptoexchange.service;

import com.cryptoexchange.dto.CryptoHolding;
import com.cryptoexchange.exception.InsufficientFundsException;
import com.cryptoexchange.model.CashBalance;
import com.cryptoexchange.model.CryptoBalance;
import com.cryptoexchange.repository.CashBalanceRepository;
import com.cryptoexchange.repository.CryptoBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BalanceService {

    private final CashBalanceRepository cashBalanceRepository;
    private final CryptoBalanceRepository cryptoBalanceRepository;

    public BalanceService(CashBalanceRepository cashBalanceRepository,
                          CryptoBalanceRepository cryptoBalanceRepository) {
        this.cashBalanceRepository = cashBalanceRepository;
        this.cryptoBalanceRepository = cryptoBalanceRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getCash() {
        return cashWallet().getAmount();
    }

    @Transactional(readOnly = true)
    public List<CryptoHolding> getHoldings() {
        return cryptoBalanceRepository.findAll().stream()
                .map(h -> new CryptoHolding(h.getSymbol(), h.getQuantity()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getCrypto(String symbol) {
        return cryptoBalanceRepository.findBySymbol(symbol)
                .map(CryptoBalance::getQuantity)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void depositCash(BigDecimal amount) {
        CashBalance wallet = cashWallet();
        wallet.setAmount(wallet.getAmount().add(amount));
    }

    @Transactional
    public void withdrawCash(BigDecimal amount) {
        CashBalance wallet = cashWallet();
        if (wallet.getAmount().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough cash to withdraw " + amount);
        }
    }

    public void requireCash(BigDecimal amount) {
        if (getCash().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough cash, need " + amount);
        }
    }

    public void requireCrypto(String symbol, BigDecimal amount) {
        if (getCrypto(symbol).compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough " + symbol + ", need " + amount);
        }
    }

    @Transactional
    public void settleBuy(String symbol, BigDecimal cost, BigDecimal quantity) {
        CashBalance wallet = cashWallet();
        wallet.setAmount(wallet.getAmount().subtract(cost));
        CryptoBalance holding = cryptoWallet(symbol);
        holding.setQuantity(holding.getQuantity().add(quantity));
    }

    @Transactional
    public void settleSell(String symbol, BigDecimal proceeds, BigDecimal quantity) {
        CashBalance wallet = cashWallet();
        wallet.setAmount(wallet.getAmount().add(proceeds));
        CryptoBalance holding = cryptoWallet(symbol);
        holding.setQuantity(holding.getQuantity().subtract(quantity));
    }

    private CashBalance cashWallet() {
        return cashBalanceRepository.findById(CashBalanceRepository.WALLET_ID)
                .orElseGet(() -> cashBalanceRepository.save(
                        new CashBalance(CashBalanceRepository.WALLET_ID, BigDecimal.ZERO)));
    }

    private CryptoBalance cryptoWallet(String symbol) {
        return cryptoBalanceRepository.findBySymbol(symbol)
                .orElseGet(() -> cryptoBalanceRepository.save(
                        new CryptoBalance(symbol, BigDecimal.ZERO)));
    }
}
