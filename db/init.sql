CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  full_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  account_no VARCHAR(20) NOT NULL,
  balance DECIMAL(12,2) NOT NULL
);

-- demo data only (plain-text passwords are for learning, never do this in real life)
INSERT INTO users (username, password, full_name) VALUES
  ('nani', 'nani123', 'Nani Kumar'),
  ('priya', 'priya123', 'Priya Sharma');

INSERT INTO accounts (username, account_no, balance) VALUES
  ('nani', 'ACME0001', 25000.50),
  ('priya', 'ACME0002', 98000.00);
