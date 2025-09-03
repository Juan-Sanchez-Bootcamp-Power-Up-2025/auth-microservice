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
  address VARCHAR(120) NOT NULL,
  document_id VARCHAR(15) NOT NULL,
  phone_number VARCHAR(15),
  base_salary NUMERIC(15) NOT NULL,
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);