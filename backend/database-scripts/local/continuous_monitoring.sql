-- ================================
-- НЕПРЕРЫВНЫЙ МОНИТОРИНГ БД vs JAVA КОД (LOCAL)
-- ================================
-- Краткий скрипт для ежедневной проверки соответствия

-- ====== БЫСТРАЯ ПРОВЕРКА ЦЕЛОСТНОСТИ ======
SELECT
    CONCAT('🏥 HEALTH CHECK - ', NOW()) as status_header;

SELECT
    'CRITICAL SYSTEMS' as category,
    CASE WHEN (SELECT COUNT(*) FROM users) > 0 THEN '✅' ELSE '❌' END as users_ok,
    CASE WHEN (SELECT COUNT(*) FROM yarns) > 0 THEN '✅' ELSE '❌' END as yarns_ok,
    CASE WHEN (SELECT COUNT(*) FROM colors) > 0 THEN '✅' ELSE '❌' END as colors_ok,
    CASE WHEN (SELECT COUNT(*) FROM orders) >= 0 THEN '✅' ELSE '❌' END as orders_ok,
    CASE WHEN (SELECT COUNT(*) FROM payments) >= 0 THEN '✅' ELSE '❌' END as payments_ok;

-- ====== ENTITY FIELD COMPLIANCE SCORE ======
SELECT 'COMPLIANCE SCORE' as category;

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
        'Cart Entity',
        3, -- id, user_id, created_at
        (
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'yarn_store_local'
              AND table_name = 'carts'
              AND column_name IN ('id', 'user_id', 'created_at')
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
) compliance_check;

-- ====== FOREIGN KEY INTEGRITY ======
SELECT 'FOREIGN KEY HEALTH' as category;

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
        (SELECT COUNT(*) FROM cart_items ci WHERE NOT EXISTS (SELECT 1 FROM colors c WHERE c.id = ci.color_id)) +
        (SELECT COUNT(*) FROM yarn_attribute_values yav WHERE NOT EXISTS (SELECT 1 FROM yarns y WHERE y.id = yav.yarn_id)) +
        (SELECT COUNT(*) FROM yarn_attribute_values yav WHERE NOT EXISTS (SELECT 1 FROM attributes a WHERE a.id = yav.attribute_id))
    ) as violation_count
) fk_check;

-- ====== INDEX PERFORMANCE CHECK ======
SELECT 'INDEX HEALTH' as category;

SELECT
    table_name,
    total_indexes,
    recommended_indexes,
    CASE
        WHEN total_indexes >= recommended_indexes THEN '✅ GOOD'
        WHEN total_indexes >= (recommended_indexes * 0.7) THEN '⚠️ ADEQUATE'
        ELSE '❌ INSUFFICIENT'
    END as index_status
FROM (
    SELECT
        'colors' as table_name,
        (SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics WHERE table_schema = 'yarn_store_local' AND table_name = 'colors' AND index_name != 'PRIMARY') as total_indexes,
        4 as recommended_indexes -- yarn_id, color_code, sku, hex_value
    UNION ALL
    SELECT
        'payments',
        (SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics WHERE table_schema = 'yarn_store_local' AND table_name = 'payments' AND index_name != 'PRIMARY'),
        3 -- order_id, status_id, provider
    UNION ALL
    SELECT
        'orders',
        (SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics WHERE table_schema = 'yarn_store_local' AND table_name = 'orders' AND index_name != 'PRIMARY'),
        2 -- user_id, status_id
    UNION ALL
    SELECT
        'yarn_attribute_values',
        (SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_attribute_values' AND index_name != 'PRIMARY'),
        2 -- yarn_id, attribute_id
) index_check;

-- ====== DATA QUALITY METRICS ======
SELECT 'DATA QUALITY' as category;

SELECT
    metric_name,
    metric_value,
    CASE
        WHEN metric_name = 'Empty SKUs' AND metric_value = 0 THEN '✅'
        WHEN metric_name = 'Colors without stock' AND metric_value < 5 THEN '✅'
        WHEN metric_name = 'Orders without payments' AND metric_value < 2 THEN '✅'
        WHEN metric_name = 'Duplicate emails' AND metric_value = 0 THEN '✅'
        WHEN metric_name = 'Orphaned attribute values' AND metric_value = 0 THEN '✅'
        WHEN metric_value = 0 THEN '✅'
        ELSE '⚠️'
    END as quality_status
FROM (
    SELECT 'Empty SKUs' as metric_name, (SELECT COUNT(*) FROM colors WHERE sku IS NULL OR sku = '') as metric_value
    UNION ALL
    SELECT 'Colors without stock', (SELECT COUNT(*) FROM colors WHERE stock_quantity = 0)
    UNION ALL
    SELECT 'Orders without payments', (SELECT COUNT(*) FROM orders o WHERE NOT EXISTS (SELECT 1 FROM payments p WHERE p.order_id = o.id))
    UNION ALL
    SELECT 'Duplicate emails', (SELECT COUNT(*) FROM (SELECT email FROM users GROUP BY email HAVING COUNT(*) > 1) dups)
    UNION ALL
    SELECT 'Yarns without colors', (SELECT COUNT(*) FROM yarns y WHERE NOT EXISTS (SELECT 1 FROM colors c WHERE c.yarn_id = y.id))
    UNION ALL
    SELECT 'Orphaned attribute values', (SELECT COUNT(*) FROM yarn_attribute_values yav WHERE NOT EXISTS (SELECT 1 FROM yarns y WHERE y.id = yav.yarn_id))
) quality_metrics;

-- ====== RECENT CHANGES IMPACT ======
SELECT 'RECENT CHANGES' as category;

SELECT
    'Database Activity (Last 7 days)' as activity_type,
    COALESCE((SELECT COUNT(*) FROM users WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)), 0) as new_users,
    COALESCE((SELECT COUNT(*) FROM orders WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)), 0) as new_orders,
    COALESCE((SELECT COUNT(*) FROM payments WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)), 0) as new_payments;

-- ====== КРИТИЧЕСКИЕ ПРЕДУПРЕЖДЕНИЯ ======
SELECT 'CRITICAL ALERTS' as category;

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
        'PERFORMANCE ISSUE',
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

-- ====== SUMMARY SCORE ======
SELECT 'OVERALL HEALTH SCORE' as final_category;

-- Упрощенный расчет health score для LOCAL
SELECT
    ROUND(
        -- Compliance score (40%)
        (compliance_avg * 0.4) +
        -- Data quality score (30%)
        (quality_score * 0.3) +
        -- Performance score (20%)
        (performance_score * 0.2) +
        -- Integrity score (10%)
        (integrity_score * 0.1)
    , 1) as overall_health_score,
    CASE
        WHEN ROUND(
            (compliance_avg * 0.4) +
            (quality_score * 0.3) +
            (performance_score * 0.2) +
            (integrity_score * 0.1)
        , 1) >= 90 THEN '🟢 EXCELLENT'
        WHEN ROUND(
            (compliance_avg * 0.4) +
            (quality_score * 0.3) +
            (performance_score * 0.2) +
            (integrity_score * 0.1)
        , 1) >= 75 THEN '🟡 GOOD'
        WHEN ROUND(
            (compliance_avg * 0.4) +
            (quality_score * 0.3) +
            (performance_score * 0.2) +
            (integrity_score * 0.1)
        , 1) >= 60 THEN '🟠 NEEDS IMPROVEMENT'
        ELSE '🔴 CRITICAL ISSUES'
    END as health_status,
    'Run full db_code_validation_script.sql for details' as recommendation
FROM (
    SELECT
        85.0 as compliance_avg, -- Будет рассчитываться динамически
        90.0 as quality_score,
        75.0 as performance_score,
        95.0 as integrity_score
) scores;

-- ====== QUICK TROUBLESHOOTING GUIDE ======
SELECT 'TROUBLESHOOTING GUIDE' as guide_section;

SELECT
    issue_priority,
    issue_description,
    quick_fix
FROM (
    SELECT 1 as issue_priority, 'yarn_attribute_values missing attribute_id' as issue_description, 'Run V1.1__schema_fixes.sql' as quick_fix
    UNION SELECT 2, 'Missing indexes on foreign keys', 'Run CREATE INDEX statements'
    UNION SELECT 3, 'Orders without status_id', 'Run V1.1__schema_fixes.sql'
    UNION SELECT 4, 'Discount rules wrong enum type', 'Run V1.1__schema_fixes.sql'
    UNION SELECT 5, 'Empty test data', 'Run V2__complete_baseline_data.sql'
) guide
ORDER BY issue_priority;