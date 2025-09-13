-- ===== SAFE FIXES FOR LOCAL DB (Compatible with MySQL 8.x) =====
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;

-- ========== Проверка и добавление id в yarn_attribute_values ==========
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'yarn_attribute_values'
    AND COLUMN_NAME = 'id'
);

SET @sql := IF(@col_exists = 0,
  'ALTER TABLE yarn_attribute_values ADD COLUMN id bigint AUTO_INCREMENT PRIMARY KEY FIRST;',
  'SELECT "id already exists"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== Переименование attr_id → attribute_id (если надо) ==========
SET @has_attr := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'yarn_attribute_values'
    AND COLUMN_NAME = 'attr_id'
);

SET @has_attribute := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'yarn_attribute_values'
    AND COLUMN_NAME = 'attribute_id'
);

SET @sql := IF(@has_attr = 1 AND @has_attribute = 0,
  'ALTER TABLE yarn_attribute_values CHANGE COLUMN attr_id attribute_id bigint NOT NULL;',
  'SELECT "No need to rename attr_id"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== discount_rules: enum fix + value rename + is_active ==========
SET @sql := 'ALTER TABLE discount_rules
  CHANGE COLUMN type type enum("PERCENTAGE","FIXED_AMOUNT","FREE_SHIPPING") NOT NULL,
  CHANGE COLUMN value_czk value decimal(12,2) NOT NULL DEFAULT 0.00;';
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Добавим is_active, если нет
SET @has_active := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'discount_rules' AND COLUMN_NAME = 'is_active'
);

SET @sql := IF(@has_active = 0,
  'ALTER TABLE discount_rules ADD COLUMN is_active BOOLEAN DEFAULT true;',
  'SELECT "is_active already exists"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== guest_tokens: expires_at ==========
SET @has_expires := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'guest_tokens' AND COLUMN_NAME = 'expires_at'
);
SET @sql := IF(@has_expires = 0,
  'ALTER TABLE guest_tokens ADD COLUMN expires_at timestamp NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 30 DAY);',
  'SELECT "expires_at already exists"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== orders: status_id ==========
SET @has_status := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'status_id'
);
SET @sql := IF(@has_status = 0,
  'ALTER TABLE orders ADD COLUMN status_id bigint NULL AFTER status;',
  'SELECT "status_id already exists"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ========== Добавим индексы (проверка по имени индекса) ==========
SET @index_check := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'colors'
    AND INDEX_NAME = 'idx_colors_yarn_id'
);
SET @sql := IF(@index_check = 0,
  'CREATE INDEX idx_colors_yarn_id ON colors(yarn_id);',
  'SELECT "index idx_colors_yarn_id exists"'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- аналогично добавим другие индексы:
-- idx_colors_stock, idx_payments_order_id, idx_orders_user_id, idx_yarn_attribute_yarn_id, idx_yarn_attribute_attribute_id

-- === Окончание ===
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;

SELECT "✅ SAFE FIXES APPLIED" AS result, NOW() AS applied_at;
