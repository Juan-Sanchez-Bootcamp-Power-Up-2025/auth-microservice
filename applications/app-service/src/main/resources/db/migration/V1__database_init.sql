CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ROLES
CREATE TABLE IF NOT EXISTS roles (
  id VARCHAR(15) PRIMARY KEY,
  name VARCHAR(60) NOT NULL UNIQUE,
  description VARCHAR(255)
);

INSERT INTO roles (id, name, description)
VALUES
('ADMIN', 'Admin', 'Admin user.'),
('CLIENT', 'General User', 'General user.'),
('CONSULTANT', 'Consultant', 'Consultant user.');

-- USERS
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(120) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  password VARCHAR(120) NOT NULL,
  role_id VARCHAR(15) NOT NULL,
  birth_date DATE,
  address VARCHAR(120),
  document_id VARCHAR(15) NOT NULL UNIQUE,
  phone_number VARCHAR(15),
  base_salary NUMERIC(15) NOT NULL,
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

INSERT INTO users (id, name, last_name, email, password, role_id, document_id, base_salary)
VALUES
('332c4918-3e7a-4d64-be0e-8191fb0c3583', 'Admin', 'User', 'admin@crediya.com', '$2a$10$gEmmSCjsm0X38BK9wzD3TeMk/O.Kp/H20RObDldrcQdojdoHvgLpG', 'ADMIN', '123456', 1234),
('f4dd2fdf-6a72-4d53-882e-bea2fad3f86b', 'Client', 'User', 'client@crediya.com', '$2a$10$ME3rrQKvjQZk5UwoTBCdYOstqOsDHB.4Fbkm3zQQiBhMT0Z0pQ40a', 'CLIENT', '1234567', 54872),
('9d7d5d28-2423-4684-acbe-ab22ad886041', 'Consultant', 'User', 'consultant@crediya.com', '$2a$10$t99iihzZ7hhwRHv7wp7Ahe6TDEBQZdIBVp8yJgsV0Qj65s4tlK7T.', 'CONSULTANT', '12345678', 1234);