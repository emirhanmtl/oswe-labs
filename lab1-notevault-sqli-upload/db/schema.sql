CREATE DATABASE IF NOT EXISTS notevault;
USE notevault;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password CHAR(40) NOT NULL, -- sha1
    role VARCHAR(16) NOT NULL DEFAULT 'user'
);

CREATE TABLE notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Admin password is a long random value the attacker is not meant to know or crack.
INSERT INTO users (username, password, role) VALUES
    ('admin', SHA1('R7$kQ2!mZ9xW#vL4pT8u'), 'admin'),
    ('alice', SHA1('alice123'), 'user');

INSERT INTO notes (user_id, title, body) VALUES
    (2, 'Groceries', 'Milk, eggs, bread'),
    (2, 'Meeting notes', 'Discuss Q3 roadmap'),
    (1, 'Admin TODO', 'Rotate API keys, review upload logs');
