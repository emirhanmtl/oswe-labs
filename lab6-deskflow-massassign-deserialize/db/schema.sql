CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash CHAR(40) NOT NULL, -- sha1
    role VARCHAR(16) NOT NULL DEFAULT 'customer', -- customer | agent | admin
    email VARCHAR(255),
    department VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES users(id),
    subject VARCHAR(255) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'open', -- open | pending | closed
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE ticket_comments (
    id SERIAL PRIMARY KEY,
    ticket_id INTEGER NOT NULL REFERENCES tickets(id),
    author_id INTEGER NOT NULL REFERENCES users(id),
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Admin password is long/random and not meant to be guessed or cracked directly.
INSERT INTO users (username, password_hash, role, email, department) VALUES
    ('admin', encode(digest('kX3!vQ9zR7#mP2wL', 'sha1'), 'hex'), 'admin', 'admin@deskflow.local', 'IT'),
    -- Onboarding default password for a newly hired agent - IT was supposed to force a
    -- reset on first login but that step got skipped for this account.
    ('agent1', encode(digest('Summer2024', 'sha1'), 'hex'), 'agent', 'agent1@deskflow.local', 'Support'),
    ('jdoe', encode(digest('jdoepass123', 'sha1'), 'hex'), 'customer', 'jdoe@example.com', NULL);

INSERT INTO tickets (customer_id, subject, status) VALUES
    (3, 'VPN keeps disconnecting', 'open'),
    (3, 'Cannot access shared drive', 'pending');

INSERT INTO ticket_comments (ticket_id, author_id, body) VALUES
    (1, 3, 'Happens every ~20 minutes, on both wifi and ethernet.'),
    (1, 2, 'Can you tell me which VPN client version you are running? @jdoe');
