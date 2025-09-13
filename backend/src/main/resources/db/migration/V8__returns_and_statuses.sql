-- Flyway Migration: V8__returns_and_statuses.sql
-- Generated: 2025-09-03 19:44:54 (FIXED)
-- Description: Inserts test return items and status histories for orders, payments, shipments

SET FOREIGN_KEY_CHECKS=0;

-- RETURNS (правильная структура из схемы)
-- Поля: id, order_id, status, refund_amount_czk, refund_amount_mdl, refund_amount_usd, reason, description, created_at, updated_at
INSERT IGNORE INTO returns (id, order_id, status, reason, description, refund_amount_mdl, refund_amount_usd, created_at, updated_at)
VALUES (501, 101, 'REQUESTED', 'Defective item', 'The yarn had visible defects and color inconsistencies', 11.98, 0.63, NOW(), NOW());

INSERT IGNORE INTO returns (id, order_id, status, reason, description, refund_amount_mdl, refund_amount_usd, created_at, updated_at)
VALUES (502, 102, 'APPROVED', 'Wrong color', 'Customer received different color than ordered', 6.99, 0.37, NOW(), NOW());

INSERT IGNORE INTO returns (id, order_id, status, reason, description, refund_amount_mdl, refund_amount_usd, created_at, updated_at)
VALUES (503, 103, 'RECEIVED', 'Size mismatch', 'Yarn thickness was different than expected', 23.97, 1.26, NOW(), NOW());

-- RETURN_ITEMS (правильная структура: return_id, color_id, quantity, reason)
INSERT IGNORE INTO return_items (return_id, color_id, quantity, reason)
VALUES (501, 1, 1, 'Color fading after wash');

INSERT IGNORE INTO return_items (return_id, color_id, quantity, reason)
VALUES (502, 2, 1, 'Wrong color delivered');

INSERT IGNORE INTO return_items (return_id, color_id, quantity, reason)
VALUES (503, 3, 2, 'Thickness not as described');
INSERT IGNORE INTO return_items (return_id, color_id, quantity, reason)
VALUES (503, 4, 1, 'Poor quality material');

-- RETURN_STATUS_HISTORY (правильная структура из схемы)
-- Поля: id, changed_at, changed_by, new_status, old_status, return_id
INSERT IGNORE INTO return_status_history (id, return_id, old_status, new_status, changed_by, changed_at)
VALUES (1, 501, 'REQUESTED', 'REQUESTED', 'system', NOW() - INTERVAL 2 DAY);

INSERT IGNORE INTO return_status_history (id, return_id, old_status, new_status, changed_by, changed_at)
VALUES (2, 502, 'REQUESTED', 'APPROVED', 'admin@bumbac.md', NOW() - INTERVAL 1 DAY);

INSERT IGNORE INTO return_status_history (id, return_id, old_status, new_status, changed_by, changed_at)
VALUES (3, 503, 'REQUESTED', 'APPROVED', 'admin@bumbac.md', NOW() - INTERVAL 3 HOUR);

INSERT IGNORE INTO return_status_history (id, return_id, old_status, new_status, changed_by, changed_at)
VALUES (4, 503, 'APPROVED', 'RECEIVED', 'system', NOW() - INTERVAL 1 HOUR);

-- ORDER_STATUS_HISTORY (правильная структура: id, order_id, from_status_id, to_status_id, changed_by, changed_at)
-- Используем status_id, а не прямое имя статуса
INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1001, 101, NULL, 1, 101, NOW() - INTERVAL 5 DAY); -- NEW

INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1002, 101, 1, 2, NULL, NOW() - INTERVAL 3 DAY); -- NEW → PAID

INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1003, 101, 2, 3, NULL, NOW() - INTERVAL 1 DAY); -- PAID → SHIPPED

INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1004, 102, NULL, 1, 102, NOW() - INTERVAL 4 DAY); -- NEW

INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1005, 102, 1, 2, NULL, NOW() - INTERVAL 2 DAY); -- NEW → PAID

INSERT IGNORE INTO order_status_history (id, order_id, from_status_id, to_status_id, changed_by, changed_at)
VALUES (1006, 103, NULL, 1, 103, NOW() - INTERVAL 6 DAY); -- NEW

-- PAYMENT_STATUS_HISTORY (правильная структура: id, payment_id, from_status_id, to_status_id, changed_at)
INSERT IGNORE INTO payment_status_history (id, payment_id, from_status_id, to_status_id, changed_at)
VALUES (2001, 101, 1, 2, NOW() - INTERVAL 3 DAY); -- PENDING → PAID

INSERT IGNORE INTO payment_status_history (id, payment_id, from_status_id, to_status_id, changed_at)
VALUES (2002, 102, 1, 2, NOW() - INTERVAL 2 DAY); -- PENDING → PAID

INSERT IGNORE INTO payment_status_history (id, payment_id, from_status_id, to_status_id, changed_at)
VALUES (2003, 103, 1, 2, NOW() - INTERVAL 1 DAY); -- PENDING → PAID

-- SHIPMENT_STATUS_HISTORY (правильная структура: id, shipment_id, from_status, to_status, changed_at, changed_by)
INSERT IGNORE INTO shipment_status_history (id, shipment_id, from_status, to_status, changed_at, changed_by)
VALUES (3001, 101, 'CREATED', 'SHIPPED', NOW() - INTERVAL 1 DAY, 'system');

INSERT IGNORE INTO shipment_status_history (id, shipment_id, from_status, to_status, changed_at, changed_by)
VALUES (3002, 102, 'CREATED', 'SHIPPED', NOW() - INTERVAL 2 DAY, 'system');

INSERT IGNORE INTO shipment_status_history (id, shipment_id, from_status, to_status, changed_at, changed_by)
VALUES (3003, 103, 'CREATED', 'SHIPPED', NOW() - INTERVAL 1 DAY, 'system');

INSERT IGNORE INTO shipment_status_history (id, shipment_id, from_status, to_status, changed_at, changed_by)
VALUES (3004, 101, 'SHIPPED', 'IN_TRANSIT', NOW() - INTERVAL 12 HOUR, 'courier');

SET FOREIGN_KEY_CHECKS=1;