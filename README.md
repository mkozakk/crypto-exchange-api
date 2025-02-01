# Crypto Exchange API

A demo cryptocurrency exchange with a simulated market, an order matching engine, real-time WebSocket feeds and a single demo wallet.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Persistence | Hibernate / Spring Data JPA, MySQL, Flyway |
| Cache | Redis |
| Realtime | WebSocket, STOMP over SockJS |
| Docs | OpenAPI / Swagger UI |
| Build | Gradle |
| Front-end | React, Vite |

## Commands

| Action | Command |
| --- | --- |
| Run the backend | `./gradlew bootRun` |
| Run the tests | `./gradlew test` |
| Build the backend | `./gradlew build` |
| Install web app dependencies | `cd webapp && npm install` |
| Run the web app | `cd webapp && npm run dev` |

## REST Endpoints

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/cryptocurrencies` | List coins and current prices |
| GET | `/api/orderbook/{symbol}` | Bids and asks for a symbol |
| POST | `/api/orders` | Place a limit or market order |
| DELETE | `/api/orders/{id}` | Cancel an open order |
| GET | `/api/transactions` | Trade history |
| GET | `/api/balance` | Cash and crypto balances |
| POST | `/api/cash/deposit` | Add cash |
| POST | `/api/cash/withdraw` | Withdraw cash |

## WebSocket Topics

| Destination | Description |
| --- | --- |
| `/ws` | STOMP over SockJS connection endpoint |
| `/topic/prices` | Live price ticks for all coins |
| `/topic/orderbook/{symbol}` | Order book updates for a symbol |

## Order Types

| Type | Behaviour |
| --- | --- |
| Limit | Placed at a specific price, rests in the book until matched or cancelled |
| Market | Executed immediately at the best available price, rejected with 400 when liquidity is insufficient |

## Configuration

| Variable | Default | Description |
| --- | --- | --- |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |

## Reference

| Tool | URL |
| --- | --- |
| API base | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
