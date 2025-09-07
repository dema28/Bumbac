-- ================================
-- ГЕНЕРАТОР АВТОМАТИЧЕСКИХ ИСПРАВЛЕНИЙ БД (LOCAL)
-- ================================
-- Этот скрипт генерирует DDL команды для приведения БД в соответствие с Entity классами

-- ====== 1. ДОБАВЛЕНИЕ НЕДОСТАЮЩИХ ПОЛЕЙ ======
SELECT '====== ADDING MISSING FIELDS ======' as action_title;

-- Генерируем команды для добавления недостающих полей
SELECT
    CONCAT(
        'ALTER TABLE ', table_name,
        ' ADD COLUMN ', missing_field, ' ',
        CASE missing_field
            WHEN 'attribute_id' THEN 'bigint NOT NULL'
            WHEN 'expires_at' THEN 'timestamp NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 30 DAY)'
            WHEN 'status_id' THEN 'bigint NULL'
            WHEN 'is_active' THEN 'boolean DEFAULT true'
            WHEN 'usage_limit' THEN 'int DEFAULT NULL'
            WHEN 'used_count' THEN 'int DEFAULT 0'
            ELSE 'varchar(255) DEFAULT NULL'
        END,
        ';'
    ) as add_field_command
FROM (
    SELECT 'yarn_attribute_values' as table_name, 'attribute_id' as missing_field
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'yarn_store_local'
          AND table_name = 'yarn_attribute_values'
          AND column_name = 'attribute_id'
    )

    UNION ALL

    SELECT 'guest_tokens', 'expires_at'
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'yarn_store_local'
          AND table_name = 'guest_tokens'
          AND column_name = 'expires_at'
    )

    UNION ALL

    SELECT 'orders', 'status_id'
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'yarn_store_local'
          AND table_name = 'orders'
          AND column_name = 'status_id'
    )

    UNION ALL

    SELECT 'discount_rules', 'is_active'
    WHERE NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'yarn_store_local'
          AND table_name = 'discount_rules'
          AND column_name = 'is_active'
    )
) missing_fields;

-- ====== 2. ИСПРАВЛЕНИЕ NAMING CONVENTIONS ======
SELECT 'FIELD RENAMING COMMANDS:' as fix_category;

-- Генерируем команды переименования полей для соответствия Entity классам
SELECT
    CONCAT(
        'ALTER TABLE ', table_name,
        ' CHANGE COLUMN ', column_name,
        ' ',
        CASE column_name
            WHEN 'attr_id' THEN 'attribute_id'
            WHEN 'color_code' THEN 'colorCode'
            WHEN 'color_name' THEN 'colorName'
            WHEN 'hex_value' THEN 'hexValue'
            WHEN 'stock_quantity' THEN 'stockQuantity'
            WHEN 'provider_tx_id' THEN 'providerTxId'
            WHEN 'amount_mdl' THEN 'amountMDL'
            WHEN 'amount_usd' THEN 'amountUSD'
            WHEN 'paid_at' THEN 'paidAt'
            WHEN 'user_id' THEN 'userId'
            WHEN 'yarn_id' THEN 'yarnId'
            WHEN 'order_id' THEN 'orderId'
            WHEN 'color_id' THEN 'colorId'
            WHEN 'status_id' THEN 'statusId'
            ELSE column_name
        END,
        ' ', data_type,
        CASE
            WHEN character_maximum_length IS NOT NULL
            THEN CONCAT('(', character_maximum_length, ')')
            WHEN numeric_precision IS NOT NULL AND numeric_scale IS NOT NULL
            THEN CONCAT('(', numeric_precision, ',', numeric_scale, ')')
            ELSE ''
        END,
        CASE WHEN is_nullable = 'NO' THEN ' NOT NULL' ELSE '' END,
        ';'
    ) as rename_command
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND column_name IN (
    'attr_id', 'color_code', 'color_name', 'hex_value', 'stock_quantity',
    'provider_tx_id', 'amount_mdl', 'amount_usd', 'paid_at',
    'user_id', 'yarn_id', 'order_id', 'color_id', 'status_id'
  )
  AND table_name IN ('colors', 'payments', 'carts', 'cart_items', 'orders', 'yarn_attribute_values')
ORDER BY table_name, column_name;

-- ====== 3. ДОБАВЛЕНИЕ НЕДОСТАЮЩИХ ИНДЕКСОВ ======
SELECT 'INDEX CREATION COMMANDS:' as fix_category;

-- Генерируем команды создания индексов из Entity аннотаций
SELECT
    CONCAT(
        'CREATE INDEX idx_', table_name, '_', column_name,
        ' ON ', table_name, ' (', column_name, ');'
    ) as create_index_command
FROM (
    SELECT 'carts' as table_name, 'user_id' as column_name
    UNION SELECT 'colors', 'yarn_id'
    UNION SELECT 'colors', 'color_code'
    UNION SELECT 'colors', 'sku'
    UNION SELECT 'colors', 'stock_quantity'
    UNION SELECT 'colors', 'price'
    UNION SELECT 'payments', 'order_id'
    UNION SELECT 'payments', 'status_id'
    UNION SELECT 'payments', 'provider'
    UNION SELECT 'payments', 'created_at'
    UNION SELECT 'cart_items', 'cart_id'
    UNION SELECT 'cart_items', 'color_id'
    UNION SELECT 'orders', 'user_id'
    UNION SELECT 'orders', 'status_id'
    UNION SELECT 'yarn_attribute_values', 'yarn_id'
    UNION SELECT 'yarn_attribute_values', 'attribute_id'
    UNION SELECT 'discount_rules', 'is_active'
    UNION SELECT 'discount_rules', 'valid_from'
) expected_indexes
WHERE NOT EXISTS (
    SELECT 1
    FROM information_schema.statistics s
    WHERE s.table_schema = 'yarn_store_local'
      AND s.table_name = expected_indexes.table_name
      AND s.column_name = expected_indexes.column_name
      AND s.index_name LIKE CONCAT('idx_%', expected_indexes.column_name, '%')
)
ORDER BY table_name, column_name;

-- ====== 4. ИСПРАВЛЕНИЕ CONSTRAINT'ОВ ======
SELECT 'CONSTRAINT FIX COMMANDS:' as fix_category;

-- Добавляем NOT NULL ограничения для полей с @NotNull
SELECT
    CONCAT(
        'ALTER TABLE ', table_name,
        ' MODIFY COLUMN ', column_name, ' ',
        data_type,
        CASE
            WHEN character_maximum_length IS NOT NULL
            THEN CONCAT('(', character_maximum_length, ')')
            WHEN numeric_precision IS NOT NULL
            THEN CONCAT('(', numeric_precision, ',', numeric_scale, ')')
            ELSE ''
        END,
        ' NOT NULL;'
    ) as add_not_null_command
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND is_nullable = 'YES'
  AND table_name IN ('colors', 'payments', 'carts', 'orders', 'yarn_attribute_values')
  AND column_name IN (
    'color_code', 'color_name', 'sku', 'stock_quantity', 'price', -- Color required fields
    'provider', 'amount_mdl', 'status_id', -- Payment required fields
    'user_id', -- Cart required fields
    'total_amount', 'status_id', -- Order required fields
    'yarn_id', 'attribute_id' -- YarnAttributeValues required fields
  )
ORDER BY table_name, column_name;

-- ====== 5. ИСПРАВЛЕНИЕ ENUM ТИПОВ ======
SELECT 'ENUM TYPE FIX COMMANDS:' as fix_category;

-- Исправляем enum типы в discount_rules
SELECT
    CONCAT(
        'ALTER TABLE discount_rules ',
        'CHANGE COLUMN type type enum("PERCENTAGE","FIXED_AMOUNT","FREE_SHIPPING") NOT NULL;'
    ) as fix_enum_command
WHERE EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'yarn_store_local'
      AND table_name = 'discount_rules'
      AND column_name = 'type'
      AND column_type LIKE '%PERCENT%'
      AND column_type NOT LIKE '%PERCENTAGE%'
);

-- ====== 6. ИСПРАВЛЕНИЕ FOREIGN KEY CASCADE ПРАВИЛ ======
SELECT 'CASCADE RULES FIX:' as fix_category;

-- Генерируем команды для исправления CASCADE правил
SELECT
    CONCAT(
        'ALTER TABLE ', kcu.table_name,
        ' DROP FOREIGN KEY ', kcu.constraint_name, ';',
        CHAR(10),
        'ALTER TABLE ', kcu.table_name,
        ' ADD CONSTRAINT ', kcu.constraint_name,
        ' FOREIGN KEY (', kcu.column_name, ')',
        ' REFERENCES ', kcu.referenced_table_name, '(', kcu.referenced_column_name, ')',
        CASE kcu.referenced_table_name
            WHEN 'users' THEN ' ON DELETE CASCADE'
            WHEN 'yarns' THEN ' ON DELETE CASCADE'
            WHEN 'orders' THEN ' ON DELETE CASCADE'
            WHEN 'carts' THEN ' ON DELETE CASCADE'
            WHEN 'attributes' THEN ' ON DELETE CASCADE'
            ELSE ' ON DELETE RESTRICT'
        END,
        ';'
    ) as fix_cascade_command
FROM information_schema.key_column_usage kcu
JOIN information_schema.referential_constraints rc
    ON kcu.constraint_name = rc.constraint_name
WHERE kcu.table_schema = 'yarn_store_local'
  AND kcu.referenced_table_name IS NOT NULL
  AND rc.delete_rule != CASE kcu.referenced_table_name
        WHEN 'users' THEN 'CASCADE'
        WHEN 'yarns' THEN 'CASCADE'
        WHEN 'orders' THEN 'CASCADE'
        WHEN 'carts' THEN 'CASCADE'
        WHEN 'attributes' THEN 'CASCADE'
        ELSE 'RESTRICT'
    END
ORDER BY kcu.table_name, kcu.column_name;

-- ====== 7. СОЗДАНИЕ НЕДОСТАЮЩИХ ТАБЛИЦ ======
SELECT 'CREATE MISSING TABLES:' as fix_category;

-- Проверяем какие таблицы отсутствуют и генерируем CREATE TABLE команды
SELECT
    missing_table,
    CASE missing_table
        WHEN 'yarn_translations' THEN
'CREATE TABLE yarn_translations (
    yarn_id bigint NOT NULL,
    locale varchar(10) NOT NULL,
    name varchar(255) NOT NULL,
    description text,
    PRIMARY KEY (yarn_id, locale),
    FOREIGN KEY (yarn_id) REFERENCES yarns(id) ON DELETE CASCADE
);'
        WHEN 'media_assets' THEN
'CREATE TABLE media_assets (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    entity_type enum("YARN","COLOR","PATTERN","BRAND") NOT NULL,
    entity_id bigint NOT NULL,
    variant enum("ORIGINAL","L","M","S","XS") NOT NULL,
    format enum("JPEG","WEBP","AVIF","PNG","PDF","MP4","ZIP") NOT NULL,
    url varchar(255) NOT NULL,
    alt_text varchar(255) DEFAULT NULL,
    sort_order int DEFAULT 0,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_entity_variant (entity_type, entity_id, variant, format),
    KEY idx_entity (entity_type, entity_id, sort_order, variant)
);'
        ELSE CONCAT('-- Create table ', missing_table, ' (structure needs to be defined)')
    END as create_table_command
FROM (
    SELECT expected_table as missing_table
    FROM (
        SELECT 'yarn_translations' as expected_table
        UNION SELECT 'media_assets'
        UNION SELECT 'patterns'
        UNION SELECT 'pattern_translations'
    ) expected
    WHERE expected_table NOT IN (
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'yarn_store_local'
    )
) missing_tables;

-- ====== 8. ПРИОРИТЕТ ИСПРАВЛЕНИЙ ======
SELECT 'EXECUTION PRIORITY:' as priority_info;

SELECT
    priority,
    fix_type,
    description,
    risk_level
FROM (
    SELECT 1 as priority, 'ADD MISSING FIELDS' as fix_type, 'Добавление недостающих полей (attribute_id, expires_at)' as description, 'LOW' as risk_level
    UNION SELECT 2, 'CREATE MISSING TABLES', 'Создание отсутствующих таблиц', 'LOW'
    UNION SELECT 3, 'ADD INDEXES', 'Создание индексов для производительности', 'LOW'
            UNION SELECT 4, 'FIX ENUM TYPES', 'Исправление enum типов (PERCENT → PERCENTAGE)', 'MEDIUM'
    UNION SELECT 5, 'ADD NOT NULL CONSTRAINTS', 'Добавление NOT NULL ограничений', 'MEDIUM'
    UNION SELECT 6, 'RENAME FIELDS', 'Переименование полей (опционально)', 'HIGH'
    UNION SELECT 7, 'FIX CASCADE RULES', 'Исправление CASCADE правил', 'HIGH'
) fixes
ORDER BY priority;

-- ====== 9. ВАЛИДАЦИЯ ПОСЛЕ ИСПРАВЛЕНИЙ ======
SELECT 'POST-FIX VALIDATION QUERIES:' as validation_category;

SELECT
'-- После применения исправлений выполните эти запросы для проверки:
SELECT "Tables count" as check_type, COUNT(*) as result FROM information_schema.tables WHERE table_schema = "yarn_store_local";
SELECT "Foreign keys count", COUNT(*) FROM information_schema.key_column_usage WHERE table_schema = "yarn_store_local" AND referenced_table_name IS NOT NULL;
SELECT "Indexes count", COUNT(*) FROM information_schema.statistics WHERE table_schema = "yarn_store_local" AND index_name != "PRIMARY";
SELECT "NOT NULL fields", COUNT(*) FROM information_schema.columns WHERE table_schema = "yarn_store_local" AND is_nullable = "NO";

-- Проверка критических полей:
SELECT "yarn_attribute_values.attribute_id exists" as check_type,
       CASE WHEN COUNT(*) > 0 THEN "YES" ELSE "NO" END as result
FROM information_schema.columns
WHERE table_schema = "yarn_store_local" AND table_name = "yarn_attribute_values" AND column_name = "attribute_id";

SELECT "discount_rules.type enum fixed" as check_type,
       CASE WHEN column_type LIKE "%PERCENTAGE%" THEN "FIXED" ELSE "NEEDS FIX" END as result
FROM information_schema.columns
WHERE table_schema = "yarn_store_local" AND table_name = "discount_rules" AND column_name = "type";

SELECT "guest_tokens.expires_at exists" as check_type,
       CASE WHEN COUNT(*) > 0 THEN "YES" ELSE "NO" END as result
FROM information_schema.columns
WHERE table_schema = "yarn_store_local" AND table_name = "guest_tokens" AND column_name = "expires_at";

SELECT "orders.status_id exists" as check_type,
       CASE WHEN COUNT(*) > 0 THEN "YES" ELSE "NO" END as result
FROM information_schema.columns
WHERE table_schema = "yarn_store_local" AND table_name = "orders" AND column_name = "status_id";' as post_fix_validation;

-- ====== 10. БЫСТРЫЕ ИСПРАВЛЕНИЯ ДЛЯ ЛОКАЛЬНОЙ РАЗРАБОТКИ ======
SELECT 'QUICK LOCAL DEVELOPMENT FIXES:' as dev_fixes;

-- Готовые команды для быстрого исправления критических проблем
SELECT
    fix_priority,
    fix_description,
    ready_command
FROM (
    SELECT 1 as fix_priority,
           'Fix yarn_attribute_values structure' as fix_description,
           'ALTER TABLE yarn_attribute_values ADD COLUMN id bigint AUTO_INCREMENT PRIMARY KEY FIRST, CHANGE COLUMN attr_id attribute_id bigint NOT NULL;' as ready_command

    UNION ALL

    SELECT 2,
           'Add expires_at to guest_tokens',
           'ALTER TABLE guest_tokens ADD COLUMN expires_at timestamp NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 30 DAY);'

    UNION ALL

    SELECT 3,
           'Add status_id to orders',
           'ALTER TABLE orders ADD COLUMN status_id bigint NULL AFTER status;'

    UNION ALL

    SELECT 4,
           'Fix discount_rules enum type',
           'ALTER TABLE discount_rules CHANGE COLUMN type type enum("PERCENTAGE","FIXED_AMOUNT","FREE_SHIPPING") NOT NULL;'

    UNION ALL

    SELECT 5,
           'Add essential indexes',
           'CREATE INDEX idx_colors_yarn_id ON colors(yarn_id); CREATE INDEX idx_payments_order_id ON payments(order_id);'
) quick_fixes
ORDER BY fix_priority;

-- ====== 11. ПОЛНЫЙ СКРИПТ ИСПРАВЛЕНИЙ ======
SELECT 'COMPLETE FIX SCRIPT (copy and execute):' as complete_script;

SELECT '
-- ===== COMPLETE LOCAL DB FIXES =====
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

-- Fix yarn_attribute_values
ALTER TABLE yarn_attribute_values
ADD COLUMN id bigint AUTO_INCREMENT PRIMARY KEY FIRST,
CHANGE COLUMN attr_id attribute_id bigint NOT NULL,
DROP PRIMARY KEY,
ADD PRIMARY KEY (id),
ADD UNIQUE KEY unique_yarn_attribute (yarn_id, attribute_id);

-- Fix discount_rules
ALTER TABLE discount_rules
CHANGE COLUMN type type enum("PERCENTAGE","FIXED_AMOUNT","FREE_SHIPPING") NOT NULL,
CHANGE COLUMN value_czk value decimal(12,2) NOT NULL DEFAULT 0.00,
ADD COLUMN is_active boolean DEFAULT true;

-- Add missing fields
ALTER TABLE guest_tokens
ADD COLUMN expires_at timestamp NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL 30 DAY);

ALTER TABLE orders
ADD COLUMN status_id bigint NULL AFTER status;

-- Add essential indexes
CREATE INDEX IF NOT EXISTS idx_colors_yarn_id ON colors(yarn_id);
CREATE INDEX IF NOT EXISTS idx_colors_stock ON colors(stock_quantity);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attribute_yarn_id ON yarn_attribute_values(yarn_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attribute_attribute_id ON yarn_attribute_values(attribute_id);

-- Update orders status_id based on enum
UPDATE orders SET status_id = CASE
   WHEN status = "NEW" THEN 1
   WHEN status = "PAID" THEN 2
   WHEN status = "SHIPPED" THEN 3
   WHEN status = "DELIVERED" THEN 4
   WHEN status = "CANCELLED" THEN 5
   WHEN status = "RETURNED" THEN 6
   ELSE 1
END;

-- Make status_id NOT NULL and add FK
ALTER TABLE orders
MODIFY COLUMN status_id bigint NOT NULL,
ADD CONSTRAINT fk_orders_status_id
FOREIGN KEY (status_id) REFERENCES order_statuses(id) ON DELETE RESTRICT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

SELECT "LOCAL DB FIXES COMPLETED" as result, NOW() as completed_at;
' as complete_fix_script;