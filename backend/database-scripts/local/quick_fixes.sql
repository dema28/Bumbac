-- ========= QUICK FIXES (idempotent / safe) =========
SET @OLD_FK = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- ----- helpers -----
SET @DB := DATABASE();

-- ========== 1) yarn_attribute_values ==========
-- 1.1: attr_id / attribute_id reconcile
SET @has_attr :=
 (SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema=@DB AND table_name='yarn_attribute_values' AND column_name='attr_id');

SET @has_attribute :=
 (SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema=@DB AND table_name='yarn_attribute_values' AND column_name='attribute_id');

-- case A: есть attr_id и нет attribute_id → переименуем
SET @sql := IF(@has_attr=1 AND @has_attribute=0,
  'ALTER TABLE yarn_attribute_values CHANGE COLUMN attr_id attribute_id BIGINT NOT NULL',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- case B: есть оба → удалим attr_id (attribute_id считаем каноничным)
SET @sql := IF(@has_attr=1 AND @has_attribute=1,
  'ALTER TABLE yarn_attribute_values DROP COLUMN attr_id',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1.2: id как PK (если нет колонки — добавим)
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS id BIGINT AUTO_INCREMENT FIRST;

-- если PK не по id — сменим
SET @pk_is_id :=
 (SELECT COUNT(*) FROM information_schema.key_column_usage
  WHERE table_schema=@DB AND table_name='yarn_attribute_values'
    AND constraint_name='PRIMARY' AND column_name='id');

SET @has_pk :=
 (SELECT COUNT(*) FROM information_schema.table_constraints
  WHERE table_schema=@DB AND table_name='yarn_attribute_values' AND constraint_type='PRIMARY KEY');

SET @sql := IF(@has_pk=1 AND @pk_is_id=0, 'ALTER TABLE yarn_attribute_values DROP PRIMARY KEY', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF(@pk_is_id=0, 'ALTER TABLE yarn_attribute_values ADD PRIMARY KEY (id)', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1.3: value (если нет)
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS value VARCHAR(255) NOT NULL DEFAULT '' AFTER attribute_id;

-- 1.4: индексы
CREATE INDEX IF NOT EXISTS idx_yarn_attr_yarn_id ON yarn_attribute_values(yarn_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attr_attr_id ON yarn_attribute_values(attribute_id);

-- уникальность пары (yarn_id, attribute_id)
SET @has_uniq :=
 (SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema=@DB AND table_name='yarn_attribute_values'
    AND index_name='unique_yarn_attribute' AND non_unique=0);

SET @sql := IF(@has_uniq=0,
  'ALTER TABLE yarn_attribute_values ADD UNIQUE KEY unique_yarn_attribute (yarn_id, attribute_id)',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;


-- ========== 2) discount_rules ==========
ALTER TABLE discount_rules
  MODIFY COLUMN type ENUM('PERCENTAGE','FIXED_AMOUNT','FREE_SHIPPING') NOT NULL;

ALTER TABLE discount_rules
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;

-- полезные индексы
CREATE INDEX IF NOT EXISTS idx_discount_rules_is_active ON discount_rules(is_active);
-- если у тебя есть поля valid_from/valid_to — не мешает:
-- CREATE INDEX IF NOT EXISTS idx_discount_rules_valid_from ON discount_rules(valid_from);


-- ========== 3) guest_tokens.expires_at ==========
ALTER TABLE guest_tokens
  ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP NULL
    DEFAULT (CURRENT_TIMESTAMP + INTERVAL 30 DAY);


-- ========== 4) orders.status_id + FK ==========
ALTER TABLE orders
  ADD COLUMN IF NOT EXISTS status_id BIGINT NULL AFTER status;

-- заполним status_id по старому enum status (если NULL)
UPDATE orders
SET status_id = CASE
   WHEN status = 'NEW' THEN 1
   WHEN status = 'PAID' THEN 2
   WHEN status = 'SHIPPED' THEN 3
   WHEN status = 'DELIVERED' THEN 4
   WHEN status = 'CANCELLED' THEN 5
   WHEN status = 'RETURNED' THEN 6
   ELSE 1
END
WHERE status_id IS NULL;

-- NOT NULL
ALTER TABLE orders MODIFY COLUMN status_id BIGINT NOT NULL;

-- добавим FK, если нет
SET @has_fk :=
 (SELECT COUNT(*) FROM information_schema.referential_constraints
  WHERE constraint_schema=@DB AND table_name='orders' AND constraint_name='fk_orders_status_id');

SET @sql := IF(@has_fk=0,
  'ALTER TABLE orders ADD CONSTRAINT fk_orders_status_id FOREIGN KEY (status_id) REFERENCES order_statuses(id) ON DELETE RESTRICT',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- индексы по orders
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status_id ON orders(status_id);


-- ========== 5) NOT NULL поля из отчёта ==========
-- carts.user_id
-- colors.color_name / colors.sku / colors.stock_quantity
-- orders.user_id
-- payments.amount_mdl / payments.provider
ALTER TABLE carts    MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE colors   MODIFY COLUMN color_name VARCHAR(128) NOT NULL DEFAULT '';
ALTER TABLE colors   MODIFY COLUMN sku        VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE colors   MODIFY COLUMN stock_quantity INT NOT NULL DEFAULT 0;
ALTER TABLE orders   MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE payments MODIFY COLUMN amount_mdl DECIMAL(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE payments MODIFY COLUMN provider   VARCHAR(64)   NOT NULL DEFAULT '';

-- индексы под платежи/цвета
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_colors_stock ON colors(stock_quantity);
CREATE INDEX IF NOT EXISTS idx_colors_yarn_id ON colors(yarn_id);

SET FOREIGN_KEY_CHECKS = @OLD_FK;

-- контрольный вывод
SELECT '✅ SAFE QUICK FIXES APPLIED' AS result, NOW() AS completed_at;
