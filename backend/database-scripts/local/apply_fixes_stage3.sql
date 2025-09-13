-- ===== STAGE 3 FIXES (DROP + CREATE indexes safely) =====
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- colors.price
SET @index_name := 'idx_colors_price';
SET @table_name := 'colors';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_colors_price ON colors (price);

-- colors.stock_quantity
SET @index_name := 'idx_colors_stock_quantity';
SET @table_name := 'colors';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_colors_stock_quantity ON colors (stock_quantity);

-- discount_rules.is_active
SET @index_name := 'idx_discount_rules_is_active';
SET @table_name := 'discount_rules';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_discount_rules_is_active ON discount_rules (is_active);

-- discount_rules.valid_from
SET @index_name := 'idx_discount_rules_valid_from';
SET @table_name := 'discount_rules';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_discount_rules_valid_from ON discount_rules (valid_from);

-- orders.status_id
SET @index_name := 'idx_orders_status_id';
SET @table_name := 'orders';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_orders_status_id ON orders (status_id);

-- yarn_attribute_values.attribute_id
SET @index_name := 'idx_yarn_attribute_values_attribute_id';
SET @table_name := 'yarn_attribute_values';
SET @sql := (
  SELECT IF(COUNT(*) > 0,
    CONCAT('DROP INDEX ', @index_name, ' ON ', @table_name),
    'SELECT "index_not_exists"')
  FROM information_schema.statistics
  WHERE table_schema = 'yarn_store_local'
    AND table_name = @table_name
    AND index_name = @index_name
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
CREATE INDEX idx_yarn_attribute_values_attribute_id ON yarn_attribute_values (attribute_id);

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

SELECT '✅ STAGE 3 INDEX FIXES APPLIED' AS result, NOW() AS applied_at;
