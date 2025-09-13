-- ================================
-- СКРИПТ ПРОВЕРКИ СООТВЕТСТВИЯ БД И КОДА
-- ================================

-- Выполните этот скрипт после применения миграции V15
-- для проверки что все проблемы исправлены

SELECT '====== ПРОВЕРКА ИСПРАВЛЕНИЙ ======' as check_title;

-- ====== 1. ПРОВЕРКА COLORS ======
SELECT 'COLORS TABLE VALIDATION' as table_check;

SELECT 
    'colors.color_name' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    CHARACTER_MAXIMUM_LENGTH as max_length
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'colors' 
AND COLUMN_NAME = 'color_name'

UNION ALL

SELECT 
    'colors.sku' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    CHARACTER_MAXIMUM_LENGTH as max_length
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'colors' 
AND COLUMN_NAME = 'sku'

UNION ALL

SELECT 
    'colors.stock_quantity' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    NUMERIC_PRECISION as precision
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'colors' 
AND COLUMN_NAME = 'stock_quantity';

-- ====== 2. ПРОВЕРКА PAYMENTS ======
SELECT 'PAYMENTS TABLE VALIDATION' as table_check;

SELECT 
    'payments.amount_mdl' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    NUMERIC_PRECISION as precision,
    NUMERIC_SCALE as scale
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'payments' 
AND COLUMN_NAME = 'amount_mdl'

UNION ALL

SELECT 
    'payments.amount_usd' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    NUMERIC_PRECISION as precision,
    NUMERIC_SCALE as scale
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'payments' 
AND COLUMN_NAME = 'amount_usd'

UNION ALL

SELECT 
    'payments.provider' as field,
    CASE WHEN IS_NULLABLE = 'NO' THEN '✅ NOT NULL' ELSE '❌ STILL NULLABLE' END as status,
    DATA_TYPE as type,
    CHARACTER_MAXIMUM_LENGTH as max_length
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'payments' 
AND COLUMN_NAME = 'provider';

-- ====== 3. ПРОВЕРКА YARN_ATTRIBUTE_VALUES ======
SELECT 'YARN_ATTRIBUTE_VALUES VALIDATION' as table_check;

SELECT 
    'yarn_attribute_values.attribute_id' as field,
    CASE WHEN COLUMN_NAME IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(DATA_TYPE, 'N/A') as type
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'yarn_attribute_values' 
AND COLUMN_NAME = 'attribute_id'

UNION ALL

SELECT 
    'yarn_attribute_values.attr_id' as field,
    CASE WHEN COLUMN_NAME IS NOT NULL THEN '❌ STILL EXISTS' ELSE '✅ REMOVED' END as status,
    COALESCE(DATA_TYPE, 'N/A') as type
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'yarn_attribute_values' 
AND COLUMN_NAME = 'attr_id';

-- ====== 4. ПРОВЕРКА CART_ITEMS ======
SELECT 'CART_ITEMS VALIDATION' as table_check;

SELECT 
    'cart_items.id' as field,
    CASE WHEN COLUMN_NAME IS NOT NULL THEN '✅ EXISTS' ELSE '❌ MISSING' END as status,
    COALESCE(DATA_TYPE, 'N/A') as type,
    COALESCE(COLUMN_KEY, 'N/A') as key_type
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'cart_items' 
AND COLUMN_NAME = 'id';

-- ====== 5. ПРОВЕРКА ИНДЕКСОВ ======
SELECT 'INDEX VALIDATION' as index_check;

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
WHERE table_schema = DATABASE()
  AND table_name IN ('colors', 'payments', 'yarn_attribute_values', 'cart_items', 'users', 'yarns')
  AND index_name != 'PRIMARY'
GROUP BY table_name, index_name, non_unique
ORDER BY table_name, index_name;

-- ====== 6. ПРОВЕРКА FOREIGN KEYS ======
SELECT 'FOREIGN KEY VALIDATION' as fk_check;

SELECT 
    'FK CHECK' as check_type,
    kcu.table_name as child_table,
    kcu.column_name as foreign_key,
    kcu.referenced_table_name as parent_table,
    rc.delete_rule,
    rc.update_rule
FROM information_schema.key_column_usage kcu
JOIN information_schema.referential_constraints rc
    ON kcu.constraint_name = rc.constraint_name
WHERE kcu.table_schema = DATABASE()
  AND kcu.referenced_table_name IS NOT NULL
ORDER BY kcu.table_name, kcu.column_name;

-- ====== 7. ИТОГОВАЯ СТАТИСТИКА ======
SELECT 'FINAL SUMMARY' as summary;

SELECT 
    'SUMMARY' as check_type,
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'colors' 
     AND COLUMN_NAME IN ('color_name', 'sku', 'stock_quantity')
     AND IS_NULLABLE = 'NO') as colors_not_null_fields,
    
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'payments' 
     AND COLUMN_NAME IN ('amount_mdl', 'amount_usd', 'provider')
     AND IS_NULLABLE = 'NO') as payments_not_null_fields,
    
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'yarn_attribute_values' 
     AND COLUMN_NAME = 'attribute_id') as yarn_attr_attribute_id_exists,
    
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME = 'cart_items' 
     AND COLUMN_NAME = 'id') as cart_items_id_exists,
    
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME IN ('colors', 'payments', 'yarn_attribute_values', 'cart_items', 'users', 'yarns')
     AND INDEX_NAME != 'PRIMARY') as total_indexes;

-- ====== 8. ПРОВЕРКА НА НАЛИЧИЕ NULL ЗНАЧЕНИЙ ======
SELECT 'NULL VALUES CHECK' as null_check;

SELECT 
    'colors NULL check' as table_name,
    COUNT(*) as total_records,
    SUM(CASE WHEN color_name IS NULL THEN 1 ELSE 0 END) as null_color_name,
    SUM(CASE WHEN sku IS NULL THEN 1 ELSE 0 END) as null_sku,
    SUM(CASE WHEN stock_quantity IS NULL THEN 1 ELSE 0 END) as null_stock_quantity
FROM colors

UNION ALL

SELECT 
    'payments NULL check' as table_name,
    COUNT(*) as total_records,
    SUM(CASE WHEN amount_mdl IS NULL THEN 1 ELSE 0 END) as null_amount_mdl,
    SUM(CASE WHEN amount_usd IS NULL THEN 1 ELSE 0 END) as null_amount_usd,
    SUM(CASE WHEN provider IS NULL THEN 1 ELSE 0 END) as null_provider
FROM payments;

-- ====== 9. ФИНАЛЬНЫЙ РЕЗУЛЬТАТ ======
SELECT '🎉 ПРОВЕРКА ЗАВЕРШЕНА!' as final_message;

