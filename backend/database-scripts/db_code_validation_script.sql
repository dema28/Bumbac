-- ================================
-- ПРОВЕРКА СООТВЕТСТВИЯ БД И JAVA КОДА (LOCAL)
-- ================================

-- ====== 1. ПРОВЕРКА СТРУКТУРЫ ТАБЛИЦ VS ENTITY ======
SELECT '====== СТРУКТУРА ТАБЛИЦ VS ENTITY КЛАССЫ ======' as check_title;

-- Проверим основные таблицы и их соответствие Entity классам
SELECT
    'TABLE STRUCTURE CHECK' as validation_type,
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_key,
    extra
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND table_name IN ('users', 'yarns', 'colors', 'orders', 'payments', 'carts', 'cart_items')
ORDER BY table_name, ordinal_position;

-- ====== 2. ПРОВЕРКА FOREIGN KEY СООТВЕТСТВИЯ ======
SELECT '====== FOREIGN KEYS VS @JoinColumn ======' as check_title;

SELECT
    'FOREIGN KEY VALIDATION' as validation_type,
    kcu.table_name,
    kcu.column_name,
    kcu.referenced_table_name,
    kcu.referenced_column_name,
    rc.update_rule,
    rc.delete_rule
FROM information_schema.key_column_usage kcu
JOIN information_schema.referential_constraints rc
    ON kcu.constraint_name = rc.constraint_name
    AND kcu.table_schema = rc.constraint_schema
WHERE kcu.table_schema = 'yarn_store_local'
  AND kcu.referenced_table_name IS NOT NULL
ORDER BY kcu.table_name, kcu.column_name;

-- ====== 3. КРИТИЧЕСКИЕ НЕСООТВЕТСТВИЯ ======
SELECT '====== КРИТИЧЕСКИЕ ПРОБЛЕМЫ ======' as issues_title;

-- Проверим существование таблиц из Entity классов
SELECT 'MISSING TABLES' as issue_type;

-- Список таблиц которые должны быть согласно Entity классам
SELECT
    expected_table,
    CASE WHEN actual_table IS NULL THEN '❌ MISSING' ELSE '✅ EXISTS' END as status
FROM (
    SELECT 'users' as expected_table
    UNION SELECT 'roles'
    UNION SELECT 'user_roles'
    UNION SELECT 'yarns'
    UNION SELECT 'colors'
    UNION SELECT 'categories'
    UNION SELECT 'brands'
    UNION SELECT 'collections'
    UNION SELECT 'carts'
    UNION SELECT 'cart_items'
    UNION SELECT 'orders'
    UNION SELECT 'order_items'
    UNION SELECT 'order_statuses'
    UNION SELECT 'payments'
    UNION SELECT 'payment_statuses'
    UNION SELECT 'attributes'
    UNION SELECT 'yarn_attribute_values'
    UNION SELECT 'yarn_translations'
) expected
LEFT JOIN (
    SELECT table_name as actual_table
    FROM information_schema.tables
    WHERE table_schema = 'yarn_store_local'
) actual ON expected.expected_table = actual.actual_table
ORDER BY expected_table;

-- ====== 4. ПРОВЕРКА СПЕЦИФИЧНЫХ ENTITY ПОЛЕЙ ======
SELECT '====== ENTITY FIELDS VALIDATION ======' as field_validation;

-- Cart Entity - проверяем поле user_id
SELECT
    'Cart.userId field' as entity_field,
    CASE
        WHEN column_name IS NOT NULL THEN '✅ EXISTS'
        ELSE '❌ MISSING'
    END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND table_name = 'carts'
  AND column_name = 'user_id'
UNION ALL

-- Color Entity - все основные поля
SELECT
    'Color.colorCode field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'colors' AND column_name = 'color_code'
UNION ALL

SELECT
    'Color.colorName field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'colors' AND column_name = 'color_name'
UNION ALL

SELECT
    'Color.hexValue field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'colors' AND column_name = 'hex_value'
UNION ALL

-- Payment Entity - MDL и USD поля
SELECT
    'Payment.amountMDL field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'payments' AND column_name = 'amount_mdl'
UNION ALL

SELECT
    'Payment.amountUSD field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'payments' AND column_name = 'amount_usd'
UNION ALL

SELECT
    'Payment.providerTxId field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'payments' AND column_name = 'provider_tx_id'
UNION ALL

-- YarnAttributeValues Entity - критически важная проверка
SELECT
    'YarnAttributeValues.attributeId field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_attribute_values' AND column_name = 'attribute_id'
UNION ALL

SELECT
    'YarnAttributeValues.id field' as entity_field,
    CASE WHEN column_name IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(data_type, 'N/A') as actual_type,
    COALESCE(is_nullable, 'N/A') as nullable
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_attribute_values' AND column_name = 'id';

-- ====== 5. ПРОВЕРКА NAMING CONVENTION НЕСООТВЕТСТВИЙ ======
SELECT '====== NAMING CONVENTION MISMATCHES ======' as naming_check;

-- Проверяем snake_case в БД vs camelCase в Entity
SELECT
    'NAMING ISSUES' as issue_type,
    table_name,
    column_name,
    CASE
        WHEN column_name LIKE '%_%' THEN
            CONCAT('Java: ',
                LOWER(SUBSTRING(column_name, 1, 1)),
                REPLACE(INITCAP(SUBSTRING(column_name, 2)), '_', '')
            )
        ELSE column_name
    END as expected_java_field
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND table_name IN ('colors', 'payments', 'cart_items', 'orders')
  AND column_name LIKE '%_%'
  AND column_name NOT IN ('created_at', 'updated_at') -- базовые поля
ORDER BY table_name, column_name;

-- ====== 6. ПРОВЕРКА ENUM VS DATABASE VALUES ======
SELECT '====== ENUM VALIDATION ======' as enum_check;

-- Проверяем статусы заказов
SELECT
    'ORDER STATUS ENUM CHECK' as validation,
    code as db_value,
    name as db_name,
    CASE
        WHEN code IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED')
        THEN '✅ MATCHES JAVA ENUM'
        ELSE '❌ NOT IN JAVA ENUM'
    END as java_compatibility
FROM order_statuses
UNION ALL

-- Проверяем статусы платежей
SELECT
    'PAYMENT STATUS ENUM CHECK' as validation,
    code as db_value,
    name as db_name,
    CASE
        WHEN code IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED', 'CANCELLED')
        THEN '✅ MATCHES JAVA ENUM'
        ELSE '❌ NOT IN JAVA ENUM'
    END as java_compatibility
FROM payment_statuses;

-- ====== 7. ПРОВЕРКА ИНДЕКСОВ ======
SELECT '====== INDEX VALIDATION ======' as index_check;

-- Проверяем существование индексов из Entity аннотаций
SELECT
    'INDEX CHECK' as check_type,
    table_name,
    index_name,
    GROUP_CONCAT(column_name ORDER BY seq_in_index) as columns,
    CASE non_unique
        WHEN 0 THEN 'UNIQUE'
        ELSE 'NON-UNIQUE'
    END as index_type
FROM information_schema.statistics
WHERE table_schema = 'yarn_store_local'
  AND table_name IN ('carts', 'colors', 'payments', 'orders', 'yarn_attribute_values')
  AND index_name != 'PRIMARY'
GROUP BY table_name, index_name, non_unique
ORDER BY table_name, index_name;

-- ====== 8. ПРОВЕРКА ВАЛИДАЦИИ (@NotNull, @NotBlank) ======
SELECT '====== VALIDATION CONSTRAINTS CHECK ======' as validation_check;

-- Проверяем NOT NULL поля которые должны быть обязательными в Entity
SELECT
    'REQUIRED FIELD VALIDATION' as check_type,
    table_name,
    column_name,
    is_nullable,
    CASE
        WHEN is_nullable = 'NO' THEN '✅ NOT NULL (matches @NotNull)'
        WHEN is_nullable = 'YES' THEN '⚠️ NULLABLE (check @NotNull annotation)'
    END as validation_status
FROM information_schema.columns
WHERE table_schema = 'yarn_store_local'
  AND table_name IN ('colors', 'payments', 'carts', 'cart_items', 'orders', 'yarn_attribute_values')
  AND column_name IN (
    'color_code', 'color_name', 'sku', 'stock_quantity', 'price', -- Color entity
    'provider', 'amount_mdl', 'status_id', -- Payment entity
    'user_id', -- Cart entity
    'order_id', 'color_id', 'quantity', 'price', -- CartItem entity
    'user_id', 'total_amount', 'status_id', -- Order entity
    'yarn_id', 'attribute_id' -- YarnAttributeValues entity
  )
ORDER BY table_name, column_name;

-- ====== 9. ПРОВЕРКА CASCADE ПРАВИЛ ======
SELECT '====== CASCADE RULES CHECK ======' as cascade_check;

SELECT
    'CASCADE VALIDATION' as check_type,
    kcu.table_name as child_table,
    kcu.column_name as foreign_key,
    kcu.referenced_table_name as parent_table,
    rc.delete_rule,
    rc.update_rule,
    CASE
        WHEN rc.delete_rule = 'CASCADE' THEN '✅ CASCADE DELETE'
        WHEN rc.delete_rule = 'SET NULL' THEN '⚠️ SET NULL'
        WHEN rc.delete_rule = 'RESTRICT' THEN '🔒 RESTRICT'
        ELSE CONCAT('❓ ', rc.delete_rule)
    END as delete_behavior
FROM information_schema.key_column_usage kcu
JOIN information_schema.referential_constraints rc
    ON kcu.constraint_name = rc.constraint_name
WHERE kcu.table_schema = 'yarn_store_local'
  AND kcu.referenced_table_name IS NOT NULL
ORDER BY kcu.table_name, kcu.column_name;

-- ====== 10. РЕЗЮМЕ ПРОБЛЕМ ======
SELECT '====== SUMMARY OF ISSUES ======' as summary;

-- Подсчет критических проблем
SELECT
    'CRITICAL ISSUES SUMMARY' as summary_type,
    (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'yarn_store_local') as existing_tables,
    20 as expected_tables, -- из Entity классов
    (SELECT COUNT(*) FROM information_schema.key_column_usage WHERE table_schema = 'yarn_store_local' AND referenced_table_name IS NOT NULL) as foreign_keys,
    (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = 'yarn_store_local' AND index_name != 'PRIMARY') as indexes
UNION ALL

SELECT
    'DATA CONSISTENCY' as summary_type,
    (SELECT COUNT(*) FROM yarns) as yarns_count,
    (SELECT COUNT(*) FROM colors) as colors_count,
    (SELECT COUNT(*) FROM orders) as orders_count,
    (SELECT COUNT(*) FROM users) as users_count;