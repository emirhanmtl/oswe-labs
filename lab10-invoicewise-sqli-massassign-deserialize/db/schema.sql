CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash CHAR(64) NOT NULL, -- sha256 hex
    full_name VARCHAR(128) NOT NULL,
    email VARCHAR(255),
    is_admin BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    owner_user_id INTEGER NOT NULL REFERENCES users(id),
    name VARCHAR(128) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(32)
);

CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES clients(id),
    owner_user_id INTEGER NOT NULL REFERENCES users(id),
    amount NUMERIC(10,2) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'unpaid',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 'jsmith' is a normal staff (sales) account. Its password is a throwaway value - how
-- you reach this account is not supposed to involve knowing it.
-- Admin's password is long/random and not meant to be guessed or cracked directly.
INSERT INTO users (username, password_hash, full_name, email, is_admin) VALUES
    ('jsmith', encode(digest('t9!qLmZ2x', 'sha256'), 'hex'), 'Jordan Smith', 'jsmith@invoicewise.local', false),
    ('admin', encode(digest('Kx8#mQ2vRz7!wL4pT9uB', 'sha256'), 'hex'), 'Administrator', 'admin@invoicewise.local', true);

INSERT INTO clients (owner_user_id, name, email, phone) VALUES
    (1, 'Acme Corp', 'billing@acme.example', '555-0100'),
    (1, 'Globex Inc', 'ap@globex.example', '555-0199');

INSERT INTO invoices (client_id, owner_user_id, amount, description, status) VALUES
    (1, 1, 1200.00, 'Website maintenance retainer - March', 'unpaid'),
    (2, 1, 450.00, 'Logo redesign', 'paid');
