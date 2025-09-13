-- Insert baseline roles
INSERT IGNORE INTO roles (id, code, name) VALUES
  (1, 'USER', 'User'),
  (2, 'ADMIN', 'Administrator');

-- Insert baseline users
INSERT IGNORE INTO users (id, email, password_hash, password_algo, first_name, last_name, phone, created_at, updated_at) VALUES
  (53, 'admin@bumbac.md', '$2a$10$IvZWQkHElPFejFRpGBx4ee.WljOEVeRHaHJBlAPxAWtnrxlGZJkH6', 'BCRYPT', 'Super', 'Admin', '+37360000000', '2025-09-03 18:05:32', '2025-09-03 18:05:32'),
  (54, 'test@bumbac.md', '$2a$10$U6/fqv.TJTeWD1uY5SFe4eAqdw4vE9edzr5MIu4IvBE6Af1AZR7Q5u', 'BCRYPT', 'Test', 'User', '+37361111111', '2025-09-03 18:05:32', '2025-09-03 18:05:32'),
  (55, 'testuserme@bumbac.md', '$2a$10$q6x39S0xcklQrH0tRFRzLOnl7K4MPx4HOEuWYPZnFVJdxEiMBdV6W', 'BCRYPT', 'Test', 'Me', '+37362222222', '2025-09-03 18:11:13', '2025-09-03 18:11:13');

-- Assign roles to users
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
  (54, 1),
  (55, 1),
  (53, 2);
