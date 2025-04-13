import React from 'react';

const fmt = (n) => Number(n).toLocaleString(undefined, { maximumFractionDigits: 6 });
const time = (t) => new Date(t).toLocaleTimeString();

export default function Transactions({ transactions }) {
  return (
    <div className="card">
      <h2>Recent trades</h2>
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Symbol</th>
            <th>Price</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          {transactions.slice(0, 15).map((tx) => (
            <tr key={tx.id}>
              <td>{time(tx.executedAt)}</td>
              <td>{tx.symbol}</td>
              <td>${fmt(tx.price)}</td>
              <td>{fmt(tx.quantity)}</td>
            </tr>
          ))}
          {transactions.length === 0 && <tr><td colSpan="4">No trades yet</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
