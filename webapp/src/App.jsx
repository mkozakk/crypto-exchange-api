import React, { useEffect, useState } from 'react';
import PriceTicker from './components/PriceTicker';
import OrderBook from './components/OrderBook';
import OrderForm from './components/OrderForm';
import Transactions from './components/Transactions';
import BalanceWidget from './components/BalanceWidget';
import { getBalance, getCryptocurrencies, getOrderBook, getTransactions } from './api';
import { subscribe } from './socket';

export default function App() {
  const [coins, setCoins] = useState([]);
  const [selected, setSelected] = useState(null);
  const [book, setBook] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [balance, setBalance] = useState(null);

  const refreshWallet = () => {
    getTransactions().then(setTransactions).catch(() => {});
    getBalance().then(setBalance).catch(() => {});
  };

  useEffect(() => {
    getCryptocurrencies().then((data) => {
      setCoins(data);
      if (data.length > 0) {
        setSelected((current) => current ?? data[0].symbol);
      }
    });
    refreshWallet();
  }, []);

  useEffect(() => {
    return subscribe('/topic/prices', (tick) => {
      setCoins((prev) =>
        prev.map((c) => (c.symbol === tick.symbol ? { ...c, currentPrice: tick.price } : c))
      );
    });
  }, []);

  useEffect(() => {
    if (!selected) {
      return undefined;
    }
    getOrderBook(selected).then(setBook).catch(() => {});
    return subscribe(`/topic/orderbook/${selected}`, setBook);
  }, [selected]);

  useEffect(() => {
    const id = setInterval(refreshWallet, 2000);
    return () => clearInterval(id);
  }, []);

  return (
    <div className="app">
      <header>
        <h1>Crypto Exchange</h1>
      </header>
      <PriceTicker coins={coins} selected={selected} onSelect={setSelected} />
      {selected && (
        <div className="grid">
          <OrderBook symbol={selected} book={book} />
          <OrderForm symbol={selected} onPlaced={refreshWallet} />
          <BalanceWidget balance={balance} onChange={refreshWallet} />
          <Transactions transactions={transactions} />
        </div>
      )}
    </div>
  );
}
