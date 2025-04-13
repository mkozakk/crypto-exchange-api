import React, { useState } from 'react';
import { depositCash, withdrawCash } from '../api';

const fmt = (n) => Number(n).toLocaleString(undefined, { maximumFractionDigits: 6 });

export default function BalanceWidget({ balance, onChange }) {
  const [amount, setAmount] = useState('');

  const run = (action) => {
    const value = Number(amount);
    if (!value) {
      return;
    }
    action(value).then(() => {
      setAmount('');
      onChange && onChange();
    });
  };

  return (
    <div className="card">
      <h2>Wallet</h2>
      <p className="cash">Cash: ${fmt(balance?.cash ?? 0)}</p>
      <table>
        <tbody>
          {(balance?.holdings ?? [])
            .filter((h) => Number(h.quantity) > 0)
            .map((h) => (
              <tr key={h.symbol}>
                <td>{h.symbol}</td>
                <td>{fmt(h.quantity)}</td>
              </tr>
            ))}
        </tbody>
      </table>
      <div className="cash-actions">
        <input
          type="number"
          step="any"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
        <button onClick={() => run(depositCash)}>Deposit</button>
        <button onClick={() => run(withdrawCash)}>Withdraw</button>
      </div>
    </div>
  );
}
