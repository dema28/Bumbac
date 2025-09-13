-- ===============================================
-- 🛠️ PREPARE FOR NOT NULL CONSTRAINTS CLEANUP
-- Generated: 2025-09-06 08:07:13
-- ===============================================

-- 💡 Убедитесь, что user с ID = 1 существует
INSERT IGNORE INTO users (id, email, password_hash, password_algo)
VALUES (1, 'test-fix-user@bumbac.local', 'dummy', 'BCRYPT');

-- 🧹 carts.user_id
SELECT 'Before carts.user_id NULLs' AS info, COUNT(*) AS null_count FROM carts WHERE user_id IS NULL;
UPDATE carts SET user_id = 1 WHERE user_id IS NULL;
SELECT 'After carts.user_id NULLs' AS info, COUNT(*) AS null_count FROM carts WHERE user_id IS NULL;

-- 🧹 orders.user_id
SELECT 'Before orders.user_id NULLs' AS info, COUNT(*) AS null_count FROM orders WHERE user_id IS NULL;
UPDATE orders SET user_id = 1 WHERE user_id IS NULL;
SELECT 'After orders.user_id NULLs' AS info, COUNT(*) AS null_count FROM orders WHERE user_id IS NULL;

-- 🧹 orders.status_id
SELECT 'Before orders.status_id NULLs' AS info, COUNT(*) AS null_count FROM orders WHERE status_id IS NULL;
UPDATE orders SET status_id = 1 WHERE status_id IS NULL;
SELECT 'After orders.status_id NULLs' AS info, COUNT(*) AS null_count FROM orders WHERE status_id IS NULL;

-- 🧹 payments.provider
SELECT 'Before payments.provider NULLs' AS info, COUNT(*) AS null_count FROM payments WHERE provider IS NULL;
UPDATE payments SET provider = 'UNSPECIFIED' WHERE provider IS NULL;
SELECT 'After payments.provider NULLs' AS info, COUNT(*) AS null_count FROM payments WHERE provider IS NULL;

-- 🧹 payments.amount_mdl
SELECT 'Before payments.amount_mdl NULLs' AS info, COUNT(*) AS null_count FROM payments WHERE amount_mdl IS NULL;
UPDATE payments SET amount_mdl = 0.00 WHERE amount_mdl IS NULL;
SELECT 'After payments.amount_mdl NULLs' AS info, COUNT(*) AS null_count FROM payments WHERE amount_mdl IS NULL;

-- 🧹 colors.color_name
SELECT 'Before colors.color_name NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE color_name IS NULL;
UPDATE colors SET color_name = 'UNNAMED' WHERE color_name IS NULL;
SELECT 'After colors.color_name NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE color_name IS NULL;

-- 🧹 colors.sku
SELECT 'Before colors.sku NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE sku IS NULL;
UPDATE colors SET sku = CONCAT('SKU-', id) WHERE sku IS NULL;
SELECT 'After colors.sku NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE sku IS NULL;

-- 🧹 colors.stock_quantity
SELECT 'Before colors.stock_quantity NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE stock_quantity IS NULL;
UPDATE colors SET stock_quantity = 0 WHERE stock_quantity IS NULL;
SELECT 'After colors.stock_quantity NULLs' AS info, COUNT(*) AS null_count FROM colors WHERE stock_quantity IS NULL;

-- ✅ Готово
SELECT '✅ NULL-FIX PREP COMPLETED' AS result, NOW() AS fixed_at;
