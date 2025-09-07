-- Flyway Migration: V7__carts_and_addresses.sql
-- Generated: 2025-09-03 19:44:34 (FIXED)
-- Description: Inserts carts, cart items, shipping addresses

SET FOREIGN_KEY_CHECKS=0;

-- CARTS (правильная структура: id, user_id, created_at, updated_at)
INSERT IGNORE INTO carts (id, user_id, created_at, updated_at)
VALUES (2101, 101, NOW(), NOW());
INSERT IGNORE INTO carts (id, user_id, created_at, updated_at)
VALUES (2102, 102, NOW(), NOW());
INSERT IGNORE INTO carts (id, user_id, created_at, updated_at)
VALUES (2103, 103, NOW(), NOW());
INSERT IGNORE INTO carts (id, user_id, created_at, updated_at)
VALUES (2104, NULL, NOW(), NOW()); -- гостевая корзина

-- CART_ITEMS (правильная структура: cart_id, color_id, quantity, added_at, created_at)
-- Используем color_id вместо yarn_id!
INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2101, 1, 2, NOW(), NOW());
INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2101, 2, 1, NOW(), NOW());

INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2102, 2, 3, NOW(), NOW());

INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2103, 3, 1, NOW(), NOW());
INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2103, 4, 2, NOW(), NOW());

-- гостевая корзина
INSERT IGNORE INTO cart_items (cart_id, color_id, quantity, added_at, created_at)
VALUES (2104, 5, 1, NOW(), NOW());

-- GUEST_TOKENS для гостевой корзины
INSERT IGNORE INTO guest_tokens (id, cart_id, token, created_at)
VALUES (1, 2104, '550e8400-e29b-41d4-a716-446655440000', NOW());

-- SHIPPING_ADDRESSES (правильная структура из схемы)
-- Структура: id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at
INSERT IGNORE INTO shipping_addresses (id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at)
VALUES (301, 101, 'SHIPPING', 'Denis Novicov', 'Stefan cel Mare 123', NULL, 'Chisinau', 'Chisinau', 'MD-2001', 'MD', '+37360000001', NOW());

INSERT IGNORE INTO shipping_addresses (id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at)
VALUES (302, 102, 'SHIPPING', 'Maria Popescu', 'Ion Mihalache 45', 'Ap. 12', 'Bucharest', 'Bucharest', '010181', 'RO', '+40212345678', NOW());

INSERT IGNORE INTO shipping_addresses (id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at)
VALUES (303, 103, 'BILLING', 'Ivan Ivanov', 'Tverskaya 7', NULL, 'Moscow', 'Moscow', '101000', 'RU', '+79161234567', NOW());

INSERT IGNORE INTO shipping_addresses (id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at)
VALUES (304, 101, 'BILLING', 'Denis Novicov', 'Stefan cel Mare 123', NULL, 'Chisinau', 'Chisinau', 'MD-2001', 'MD', '+37360000001', NOW());

INSERT IGNORE INTO shipping_addresses (id, user_id, address_type, recipient, street_1, street_2, city, region, postal_code, country, phone, created_at)
VALUES (305, 102, 'SHIPPING', 'Maria Popescu', 'Calea Victoriei 200', NULL, 'Bucharest', 'Bucharest', '010026', 'RO', '+40212345679', NOW());

SET FOREIGN_KEY_CHECKS=1;