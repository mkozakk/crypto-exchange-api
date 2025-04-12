const json = (res) => {
  if (!res.ok) {
    return res.json().then((body) => {
      throw new Error(body.message || res.statusText);
    });
  }
  return res.json();
};

export const getCryptocurrencies = () => fetch('/api/cryptocurrencies').then(json);

export const getOrderBook = (symbol) => fetch(`/api/orderbook/${symbol}`).then(json);

export const getTransactions = (symbol) =>
  fetch(`/api/transactions${symbol ? `?symbol=${symbol}` : ''}`).then(json);

export const getBalance = () => fetch('/api/balance').then(json);

export const placeOrder = (order) =>
  fetch('/api/orders', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(order),
  }).then(json);

export const depositCash = (amount) =>
  fetch('/api/cash/deposit', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ amount }),
  }).then(json);

export const withdrawCash = (amount) =>
  fetch('/api/cash/withdraw', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ amount }),
  }).then(json);
