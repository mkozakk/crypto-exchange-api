CREATE TABLE cryptocurrency (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    symbol        VARCHAR(16)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    current_price DECIMAL(30, 8) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cryptocurrency_symbol (symbol)
);

CREATE TABLE orders (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    symbol             VARCHAR(16)  NOT NULL,
    side               VARCHAR(8)   NOT NULL,
    type               VARCHAR(8)   NOT NULL,
    price              DECIMAL(30, 8) NULL,
    quantity           DECIMAL(30, 8) NOT NULL,
    remaining_quantity DECIMAL(30, 8) NOT NULL,
    status             VARCHAR(12)  NOT NULL,
    source             VARCHAR(12)  NOT NULL,
    created_at         DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_orders_book (symbol, side, status, price)
);

CREATE TABLE transaction (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    symbol        VARCHAR(16)  NOT NULL,
    buy_order_id  BIGINT       NOT NULL,
    sell_order_id BIGINT       NOT NULL,
    price         DECIMAL(30, 8) NOT NULL,
    quantity      DECIMAL(30, 8) NOT NULL,
    executed_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    KEY idx_transaction_symbol (symbol, executed_at)
);

CREATE TABLE cash_balance (
    id     BIGINT       NOT NULL,
    amount DECIMAL(30, 8) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE crypto_balance (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    symbol   VARCHAR(16)  NOT NULL,
    quantity DECIMAL(30, 8) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_crypto_balance_symbol (symbol)
);
