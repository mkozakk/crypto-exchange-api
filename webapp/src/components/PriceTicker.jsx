import React from 'react';

export default function PriceTicker({ coins, selected, onSelect }) {
  return (
    <div className="ticker">
      {coins.map((coin) => (
        <button
          key={coin.symbol}
          className={`ticker-item ${coin.symbol === selected ? 'active' : ''}`}
          onClick={() => onSelect(coin.symbol)}
        >
          <span className="ticker-symbol">{coin.symbol}</span>
          <span className="ticker-price">${Number(coin.currentPrice).toLocaleString()}</span>
        </button>
      ))}
    </div>
  );
}
