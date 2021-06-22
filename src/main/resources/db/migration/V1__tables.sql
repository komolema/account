DROP TABLE IF EXISTS Transaction;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS Customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


CREATE TABLE Customer
(
    id      uuid DEFAULT uuid_generate_v4(),
    name    VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Account
(
    id          uuid DEFAULT uuid_generate_v4(),
    balance     DECIMAL NOT NULL,
    customer_id uuid,
    account_type INTEGER NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
            REFERENCES Customer (id)
);