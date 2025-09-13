-- ============================
-- APPLY FIXES: STAGE 2 (safe)
-- Enforce NOT NULL constraints + fix FK rules
-- ============================

SET @OLD_FK := @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- Удалим FK, мешающий user_id NOT NULL
ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_1;

-- carts.user_id → NOT NULL
ALTER TABLE carts
  MODIFY COLUMN user_id BIGINT NOT NULL;

-- colors fields → NOT NULL
ALTER TABLE colors
  MODIFY COLUMN color_name VARCHAR(128) NOT NULL,
  MODIFY COLUMN sku VARCHAR(100) NOT NULL,
  MODIFY COLUMN stock_quantity INT NOT NULL;

-- orders fields → NOT NULL
ALTER TABLE orders
  MODIFY COLUMN user_id BIGINT NOT NULL,
  MODIFY COLUMN status_id BIGINT NOT NULL;

-- payments fields → NOT NULL
ALTER TABLE payments
  MODIFY COLUMN provider VARCHAR(64) NOT NULL,
  MODIFY COLUMN amount_mdl DECIMAL(12,2) NOT NULL;

-- Восстановим FK с новым поведением
ALTER TABLE orders
  ADD CONSTRAINT orders_ibfk_1
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = @OLD_FK;

SELECT '✅ STAGE 2 FIXES APPLIED' AS result, NOW() AS applied_at;
