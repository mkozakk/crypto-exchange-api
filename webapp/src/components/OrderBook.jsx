import React from 'react';

const fmt = (n) => Number(n).toLocaleString(undefined, { maximumFractionDigits: 6 });

export default function OrderBook({ symbol, book }) {
  const bids = book?.bids ?? [];
  const asks = book?.asks ?? [];

  return (
    <div className="card">
      <h2>Order book · {symbol}</h2>
      <div className="orderbook">
        <table>
          <thead>
            <tr>
              <th>Bid price</th>
              <th>Size</th>
            </tr>
          </thead>
          <tbody>
            {bids.slice(0, 10).map((level, i) => (
              <tr key={i} className="bid">
                <td>${fmt(level.price)}</td>
                <td>{fmt(level.quantity)}</td>
              </tr>
            ))}
            {bids.length === 0 && <tr><td colSpan="2">No bids</td></tr>}
          </tbody>
        </table>
        <table>
          <thead>
            <tr>
              <th>Ask price</th>
              <th>Size</th>
            </tr>
          </thead>
          <tbody>
            {asks.slice(0, 10).map((level, i) => (
              <tr key={i} className="ask">
                <td>${fmt(level.price)}</td>
                <td>{fmt(level.quantity)}</td>
              </tr>
            ))}
            {asks.length === 0 && <tr><td colSpan="2">No asks</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}
