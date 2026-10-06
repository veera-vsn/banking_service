CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,          -- salted PBKDF2 hash (iterations:salt:hash), never plain text
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) UNIQUE NOT NULL,    -- one account per user
  account_no VARCHAR(20) NOT NULL,
  balance DECIMAL(12,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- demo users: nani / nani123   and   priya / priya123   (stored as hashes)
INSERT INTO users (username, password, full_name, email) VALUES
  ('nani',  '120000:ej+IH8KUU5UKllx0Md8zjQ==:aukxwh+k3LWsIgwcfV3LDtm2ZiviKxPcfRBV1pPVCuY=',  'Nani Kumar',   'nani@example.com'),
  ('priya', '120000:SYEhE0rgVn1LqiVOSWjA4A==:AUCUCgrQTwFPdhqu0jg3tlW59RSRG/bYS9QQcirT70Y=', 'Priya Sharma', 'priya@example.com');

INSERT INTO accounts (username, account_no, balance) VALUES
  ('nani',  'ACME0001', 25000.50),
  ('priya', 'ACME0002', 98000.00);
