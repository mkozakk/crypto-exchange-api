import React, { useState } from 'react';
import { placeOrder } from '../api';

export default function OrderForm({ symbol, onPlaced }) {
  const [side, setSide] = useState('BUY');
  const [type, setType] = useState('LIMIT');
  const [price, setPrice] = useState('');
  const [quantity, setQuantity] = useState('');
  const [error, setError] = useState(null);

  const submit = (e) => {
    e.preventDefault();
    setError(null);
    const order = {
      side,
      type,
      symbol,
      quantity: Number(quantity),
    };
    if (type === 'LIMIT') {
      order.price = Number(price);
    }
    placeOrder(order)
      .then(() => {
        setPrice('');
        setQuantity('');
        onPlaced && onPlaced();
      })
      .catch((err) => setError(err.message));
  };

  return (
    <form className="card" onSubmit={submit}>
      <h2>Place order · {symbol}</h2>
      <div className="toggle">
        <button type="button" className={side === 'BUY' ? 'buy active' : ''} onClick={() => setSide('BUY')}>Buy</button>
        <button type="button" className={side === 'SELL' ? 'sell active' : ''} onClick={() => setSide('SELL')}>Sell</button>
      </div>
      <div className="toggle">
        <button type="button" className={type === 'LIMIT' ? 'active' : ''} onClick={() => setType('LIMIT')}>Limit</button>
        <button type="button" className={type === 'MARKET' ? 'active' : ''} onClick={() => setType('MARKET')}>Market</button>
      </div>
      {type === 'LIMIT' && (
        <label>
          Price
          <input type="number" step="any" value={price} onChange={(e) => setPrice(e.target.value)} required />
        </label>
      )}
      <label>
        Quantity
        <input type="number" step="any" value={quantity} onChange={(e) => setQuantity(e.target.value)} required />
      </label>
      <button type="submit" className={`submit ${side.toLowerCase()}`}>{side} {symbol}</button>
      {error && <p className="error">{error}</p>}
    </form>
  );
}
