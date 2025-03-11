package com.cryptoexchange.simulation;

import com.cryptoexchange.model.CryptoBalance;
import com.cryptoexchange.model.Cryptocurrency;
import com.cryptoexchange.repository.CryptoBalanceRepository;
import com.cryptoexchange.repository.CryptocurrencyRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CoinSeeder implements ApplicationRunner {

    private record Coin(String symbol, String name, String startPrice) {
    }

    private static final List<Coin> COINS = List.of(
            new Coin("BTC", "Bitcoin", "45000"),
            new Coin("ETH", "Ethereum", "2500"),
            new Coin("SOL", "Solana", "150"),
            new Coin("XRP", "Ripple", "0.50"),
            new Coin("ADA", "Cardano", "0.40"),
            new Coin("DOGE", "Dogecoin", "0.08"));

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final CryptoBalanceRepository cryptoBalanceRepository;

    public CoinSeeder(CryptocurrencyRepository cryptocurrencyRepository,
                      CryptoBalanceRepository cryptoBalanceRepository) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.cryptoBalanceRepository = cryptoBalanceRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Coin coin : COINS) {
            if (!cryptocurrencyRepository.existsBySymbol(coin.symbol())) {
                cryptocurrencyRepository.save(new Cryptocurrency(
                        coin.symbol(), coin.name(), new BigDecimal(coin.startPrice())));
            }
            if (cryptoBalanceRepository.findBySymbol(coin.symbol()).isEmpty()) {
                cryptoBalanceRepository.save(new CryptoBalance(coin.symbol(), BigDecimal.ZERO));
            }
        }
    }
}
