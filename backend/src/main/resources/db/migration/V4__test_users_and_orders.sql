-- Flyway Migration: V4__test_users_and_orders.sql
-- Generated: 2025-09-03 19:36:19 (FIXED)
-- Description: Inserts test users, favorites, orders, payments, shipments

SET FOREIGN_KEY_CHECKS=0;

-- USERS (используем правильную структуру таблицы users)
INSERT IGNORE INTO users (id, email, password_hash, password_algo, first_name, last_name, phone, created_at, updated_at)
VALUES (101, 'testuser1@bumbac.md', '$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca', 'BCRYPT', 'Test', 'User1', '+37360000101', NOW(), NOW());

INSERT IGNORE INTO users (id, email, password_hash, password_algo, first_name, last_name, phone, created_at, updated_at)
VALUES (102, 'testuser2@bumbac.md', '$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca', 'BCRYPT', 'Test', 'User2', '+37360000102', NOW(), NOW());

INSERT IGNORE INTO users (id, email, password_hash, password_algo, first_name, last_name, phone, created_at, updated_at)
VALUES (103, 'testuser3@bumbac.md', '$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca', 'BCRYPT', 'Test', 'User3', '+37360000103', NOW(), NOW());

-- РОЛИ для новых пользователей
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (101, 1);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (102, 1);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (103, 1);

-- COLORS (нужны для order_items, так как order_items ссылается на color_id)
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity)
VALUES (1, 1, 'C001', 'White', 5.99, 100);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity)
VALUES (2, 2, 'C001', 'Natural', 6.99, 100);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity)
VALUES (3, 3, 'C001', 'Pink', 7.99, 100);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity)
VALUES (4, 4, 'C001', 'Grey', 8.99, 100);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity)
VALUES (5, 5, 'C001', 'Blue', 9.99, 100);

-- USER_FAVORITES (используем правильную структуру)
INSERT IGNORE INTO user_favorites (user_id, color_id, yarn_id, added_at, created_at, updated_at)
VALUES (101, 1, 1, NOW(), NOW(), NOW());
INSERT IGNORE INTO user_favorites (user_id, color_id, yarn_id, added_at, created_at, updated_at)
VALUES (102, 2, 2, NOW(), NOW(), NOW());
INSERT IGNORE INTO user_favorites (user_id, color_id, yarn_id, added_at, created_at, updated_at)
VALUES (103, 3, 3, NOW(), NOW(), NOW());

-- ORDERS (используем правильную структуру)
INSERT IGNORE INTO orders (id, user_id, status, total_amount, created_at, updated_at)
VALUES (101, 101, 'NEW', 20.99, NOW(), NOW());
INSERT IGNORE INTO orders (id, user_id, status, total_amount, created_at, updated_at)
VALUES (102, 102, 'NEW', 21.99, NOW(), NOW());
INSERT IGNORE INTO orders (id, user_id, status, total_amount, created_at, updated_at)
VALUES (103, 103, 'NEW', 22.99, NOW(), NOW());

-- ORDER_ITEMS (используем правильную структуру: color_id вместо yarn_id)
INSERT IGNORE INTO order_items (order_id, color_id, quantity, unit_price, total_price, created_at, updated_at)
VALUES (101, 1, 2, 5.99, 11.98, NOW(), NOW());
INSERT IGNORE INTO order_items (order_id, color_id, quantity, unit_price, total_price, created_at, updated_at)
VALUES (102, 2, 1, 6.99, 6.99, NOW(), NOW());
INSERT IGNORE INTO order_items (order_id, color_id, quantity, unit_price, total_price, created_at, updated_at)
VALUES (103, 3, 3, 7.99, 23.97, NOW(), NOW());

-- PAYMENT_STATUSES (если не существуют)
INSERT IGNORE INTO payment_statuses (id, code, name) VALUES (1, 'PENDING', 'Pending');
INSERT IGNORE INTO payment_statuses (id, code, name) VALUES (2, 'PAID', 'Paid');
INSERT IGNORE INTO payment_statuses (id, code, name) VALUES (3, 'FAILED', 'Failed');
INSERT IGNORE INTO payment_statuses (id, code, name) VALUES (4, 'REFUNDED', 'Refunded');

-- PAYMENTS (используем правильную структуру)
INSERT IGNORE INTO payments (id, order_id, status_id, provider, amount_mdl, amount_usd, currency, payment_method, created_at, updated_at)
VALUES (101, 101, 2, 'stripe', 20.99, 1.10, 'MDL', 'card', NOW(), NOW());
INSERT IGNORE INTO payments (id, order_id, status_id, provider, amount_mdl, amount_usd, currency, payment_method, created_at, updated_at)
VALUES (102, 102, 2, 'stripe', 21.99, 1.15, 'MDL', 'card', NOW(), NOW());
INSERT IGNORE INTO payments (id, order_id, status_id, provider, amount_mdl, amount_usd, currency, payment_method, created_at, updated_at)
VALUES (103, 103, 2, 'stripe', 22.99, 1.20, 'MDL', 'card', NOW(), NOW());

-- SHIPPING_METHODS (если не существуют)
INSERT IGNORE INTO shipping_methods (id, carrier, service, base_price_czk, delivery_days_min, delivery_days_max, name)
VALUES (1, 'Post', 'Standard', 5.00, 3, 7, 'Обычная почта');
INSERT IGNORE INTO shipping_methods (id, carrier, service, base_price_czk, delivery_days_min, delivery_days_max, name)
VALUES (2, 'Courier', 'Express', 15.00, 1, 2, 'Курьерская доставка');

-- SHIPMENTS
INSERT IGNORE INTO shipments (id, order_id, method_id, tracking_no, status, shipped_at)
VALUES (101, 101, 1, 'TRACK-1001', 'SHIPPED', NOW());
INSERT IGNORE INTO shipments (id, order_id, method_id, tracking_no, status, shipped_at)
VALUES (102, 102, 2, 'TRACK-1002', 'SHIPPED', NOW());
INSERT IGNORE INTO shipments (id, order_id, method_id, tracking_no, status, shipped_at)
VALUES (103, 103, 1, 'TRACK-1003', 'SHIPPED', NOW());

SET FOREIGN_KEY_CHECKS=1;