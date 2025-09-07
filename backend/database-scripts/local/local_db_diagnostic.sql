-- ================================
-- ДИАГНОСТИКА LOCAL БАЗЫ ДАННЫХ yarn_store_local
-- ================================

-- ====== 1. БЫСТРАЯ ПРОВЕРКА ИСПРАВЛЕНИЙ ======
SELECT
    'POST-FIX VALIDATION FOR LOCAL' as check_title,
    NOW() as check_time;

-- Проверяем ключевые исправления
SELECT
    'yarn_attribute_values.attribute_id' as fix_check,
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.columns
              WHERE table_schema = 'yarn_store_local'
                AND table_name = 'yarn_attribute_values'
                AND column_name = 'attribute_id') > 0
        THEN '✅ FIXED'
        ELSE '❌ STILL BROKEN'
    END as status
UNION ALL
SELECT
    'discount_rules.type enum',
    CASE
        WHEN (SELECT column_type FROM information_schema.columns
              WHERE table_schema = 'yarn_store_local'
                AND table_name = 'discount_rules'
                AND column_name = 'type') LIKE '%PERCENTAGE%'
        THEN '✅ FIXED'
        ELSE '❌ STILL BROKEN'
    END
UNION ALL
SELECT
    'guest_tokens.expires_at',
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.columns
              WHERE table_schema = 'yarn_store_local'
                AND table_name = 'guest_tokens'
                AND column_name = 'expires_at') > 0
        THEN '✅ FIXED'
        ELSE '❌ STILL BROKEN'
    END
UNION ALL
SELECT
    'orders.status_id',
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.columns
              WHERE table_schema = 'yarn_store_local'
                AND table_name = 'orders'
                AND column_name = 'status_id') > 0
        THEN '✅ FIXED'
        ELSE '❌ STILL BROKEN'
    END;

-- ====== 2. ОБЩАЯ СТАТИСТИКА ТАБЛИЦ ======
SELECT '====== ОБЩАЯ СТАТИСТИКА ======' as section;

SELECT
    'ОСНОВНЫЕ СУЩНОСТИ' as category,
    '' as table_name,
    '' as count,
    '' as details
UNION ALL
SELECT '', 'users', COUNT(*), CONCAT('Roles: ', (SELECT COUNT(DISTINCT role_id) FROM user_roles)) FROM users
UNION ALL
SELECT '', 'yarns', COUNT(*), CONCAT('Collections: ', (SELECT COUNT(DISTINCT collection_id) FROM yarns WHERE collection_id IS NOT NULL)) FROM yarns
UNION ALL
SELECT '', 'colors', COUNT(*), CONCAT('With stock: ', (SELECT COUNT(*) FROM colors WHERE stock_quantity > 0)) FROM colors
UNION ALL
SELECT '', 'categories', COUNT(*), CONCAT('Active: ', (SELECT COUNT(*) FROM categories)) FROM categories
UNION ALL
SELECT '', 'brands', COUNT(*), CONCAT('Active: ', (SELECT COUNT(*) FROM brands)) FROM brands
UNION ALL
SELECT '', 'collections', COUNT(*), CONCAT('Active: ', (SELECT COUNT(*) FROM collections)) FROM collections

UNION ALL
SELECT
    'КАТАЛОГ И АТРИБУТЫ' as category,
    '' as table_name,
    '' as count,
    '' as details
UNION ALL
SELECT '', 'attributes', COUNT(*), CONCAT('Types: ', (SELECT COUNT(DISTINCT data_type) FROM attributes)) FROM attributes
UNION ALL
SELECT '', 'yarn_attribute_values', COUNT(*), CONCAT('Yarns with attrs: ', (SELECT COUNT(DISTINCT yarn_id) FROM yarn_attribute_values)) FROM yarn_attribute_values
UNION ALL
SELECT '', 'yarn_translations', COUNT(*), CONCAT('Languages: ', (SELECT COUNT(DISTINCT locale) FROM yarn_translations)) FROM yarn_translations

UNION ALL
SELECT
    'ЗАКАЗЫ И ПЛАТЕЖИ' as category,
    '' as table_name,
    '' as count,
    '' as details
UNION ALL
SELECT '', 'orders', COUNT(*), CONCAT('Status types: ', (SELECT COUNT(DISTINCT status) FROM orders)) FROM orders
UNION ALL
SELECT '', 'order_items', COUNT(*), CONCAT('Orders with items: ', (SELECT COUNT(DISTINCT order_id) FROM order_items)) FROM order_items
UNION ALL
SELECT '', 'payments', COUNT(*), CONCAT('Paid: ', (SELECT COUNT(*) FROM payments WHERE status_id = 2)) FROM payments;

-- ====== 3. ПРОВЕРКА ENTITY COMPLIANCE ======
SELECT '====== ENTITY COMPLIANCE CHECK ======' as section;

SELECT
    entity_name,
    fields_expected,
    fields_existing,
    ROUND((fields_existing * 100.0 / fields_expected), 1) as compliance_percentage,
    CASE
        WHEN (fields_existing * 100.0 / fields_expected) >= 95 THEN '🟢 EXCELLENT'
        WHEN (fields_existing * 100.0 / fields_expected) >= 80 THEN '🟡 GOOD'
        WHEN (fields_existing * 100.0 / fields_expected) >= 60 THEN '🟠 NEEDS WORK'
        ELSE '🔴 CRITICAL'
    END as status
FROM (
    SELECT
        'Colors Entity' as entity_name,
        8 as fields_expected, -- id, color_code, color_name, sku, hex_value, stock_quantity, price, yarn_id
        (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'yarn_store_local'
              AND table_name = 'colors'
              AND column_name IN ('id', 'color_code', 'color_name', 'sku', 'hex_value', 'stock_quantity', 'price', 'yarn_id')
        ) as fields_existing

    UNION ALL

    SELECT
        'Payment Entity',
        7, -- id, order_id, status_id, provider, provider_tx_id, amount_mdl, amount_usd
        (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'yarn_store_local'
              AND table_name = 'payments'
              AND column_name IN ('id', 'order_id', 'status_id', 'provider', 'provider_tx_id', 'amount_mdl', 'amount_usd')
        )

    UNION ALL

    SELECT
        'YarnAttributeValues Entity',
        4, -- id, yarn_id, attribute_id, value
        (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'yarn_store_local'
              AND table_name = 'yarn_attribute_values'
              AND column_name IN ('id', 'yarn_id', 'attribute_id', 'value')
        )

    UNION ALL

    SELECT
        'Order Entity',
        6, -- id, user_id, status_id, total_amount, created_at, updated_at
        (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'yarn_store_local'
              AND table_name = 'orders'
              AND column_name IN ('id', 'user_id', 'status_id', 'total_amount', 'created_at', 'updated_at')
        )
) compliance_check;

-- ====== 4. FOREIGN KEY INTEGRITY ======
SELECT '====== FOREIGN KEY HEALTH ======' as section;

SELECT
    'FK Violations Check' as check_type,
    COALESCE(violation_count, 0) as violations_found,
    CASE
        WHEN COALESCE(violation_count, 0) = 0 THEN '✅ NO VIOLATIONS'
        WHEN COALESCE(violation_count, 0) < 5 THEN '⚠️ FEW VIOLATIONS'
        ELSE '❌ MANY VIOLATIONS'
    END as status
FROM (
    SELECT (
        -- Проверяем основные FK нарушения
        (SELECT COUNT(*) FROM colors c WHERE NOT EXISTS (SELECT 1 FROM yarns y WHERE y.id = c.yarn_id)) +
        (SELECT COUNT(*) FROM orders o WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.id = o.user_id)) +
        (SELECT COUNT(*) FROM payments p WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.id = p.order_id)) +
        (SELECT COUNT(*) FROM cart_items ci WHERE NOT EXISTS (SELECT 1 FROM colors c WHERE c.id = ci.color_id))
    ) as violation_count
) fk_check;

-- ====== 5. CRITICAL ISSUES ======
SELECT '====== CRITICAL ISSUES ======' as section;

-- Проверяем критические проблемы которые могут сломать приложение
SELECT
    alert_type,
    alert_message,
    severity
FROM (
    SELECT
        'MISSING CORE TABLE' as alert_type,
        CONCAT('Table missing: ', missing_table) as alert_message,
        'CRITICAL' as severity
    FROM (
        SELECT expected_table as missing_table
        FROM (
            SELECT 'users' as expected_table
            UNION SELECT 'yarns'
            UNION SELECT 'colors'
            UNION SELECT 'orders'
            UNION SELECT 'payments'
            UNION SELECT 'yarn_attribute_values'
            UNION SELECT 'discount_rules'
        ) core_tables
        WHERE expected_table NOT IN (
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'yarn_store_local'
        )
    ) missing

    UNION ALL

    SELECT
        'NULLABLE REQUIRED FIELD',
        CONCAT('Field ', table_name, '.', column_name, ' is nullable but should be NOT NULL'),
        'HIGH'
    FROM information_schema.columns
    WHERE table_schema = 'yarn_store_local'
      AND is_nullable = 'YES'
      AND (
          (table_name = 'colors' AND column_name IN ('color_code', 'color_name', 'sku', 'stock_quantity', 'price'))
          OR (table_name = 'payments' AND column_name IN ('provider', 'amount_mdl', 'status_id'))
          OR (table_name = 'carts' AND column_name = 'user_id')
          OR (table_name = 'orders' AND column_name IN ('user_id', 'total_amount', 'status_id'))
          OR (table_name = 'yarn_attribute_values' AND column_name IN ('yarn_id', 'attribute_id'))
      )

    UNION ALL

    SELECT
        'MISSING INDEX',
        CONCAT('Missing index on ', table_name, '.', column_name),
        'MEDIUM'
    FROM (
        SELECT 'colors' as table_name, 'yarn_id' as column_name
        UNION SELECT 'payments', 'order_id'
        UNION SELECT 'orders', 'user_id'
        UNION SELECT 'cart_items', 'cart_id'
        UNION SELECT 'yarn_attribute_values', 'yarn_id'
        UNION SELECT 'yarn_attribute_values', 'attribute_id'
    ) expected_indexes
    WHERE NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics s
        WHERE s.table_schema = 'yarn_store_local'
          AND s.table_name = expected_indexes.table_name
          AND s.column_name = expected_indexes.column_name
          AND s.index_name != 'PRIMARY'
    )
) alerts
ORDER BY
    CASE severity
        WHEN 'CRITICAL' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'MEDIUM' THEN 3
        ELSE 4
    END;

-- ====== 6. NEXT STEPS ======
SELECT '====== RECOMMENDED NEXT STEPS ======' as section;

SELECT
    step_priority,
    step_description,
    estimated_time
FROM (
    SELECT 1 as step_priority, 'Apply V2__complete_baseline_data.sql' as step_description, '5 min' as estimated_time
    UNION SELECT 2, 'Run application startup test', '2 min'
    UNION SELECT 3, 'Test API endpoints', '10 min'
    UNION SELECT 4, 'Run complete validation script', '5 min'
    UNION SELECT 5, 'Prepare for staging migration', '15 min'
) steps
ORDER BY step_priority;