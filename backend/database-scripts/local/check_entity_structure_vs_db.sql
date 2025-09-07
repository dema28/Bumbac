-- ПОЛНЫЙ скрипт проверки структуры БД yarn_store_local (все 44 таблицы)
-- Обновлен: 2025-09-07

-- 1. RefreshToken ✅
SELECT 'RefreshToken' as entity, 'refresh_token' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'token', 'user_id', 'expiry') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'refresh_token';

-- 2. Role ✅
SELECT 'Role' as entity, 'roles' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'code', 'name') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'roles';

-- 3. User ✅
SELECT 'User' as entity, 'users' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'email', 'password_hash', 'password_algo', 'first_name', 'last_name', 'phone', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'users';

-- 4. Cart ✅
SELECT 'Cart' as entity, 'carts' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'user_id', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'carts';

-- 5. CartItem ✅ (исправлено)
SELECT 'CartItem' as entity, 'cart_items' as table_name, column_name,
  CASE WHEN column_name IN ('cart_id', 'color_id', 'quantity', 'added_at', 'created_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'cart_items';

-- 6. Color ✅
SELECT 'Color' as entity, 'colors' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'yarn_id', 'color_code', 'color_name', 'sku', 'barcode', 'hex_value', 'stock_quantity', 'price', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'colors';

-- 7. Category ✅
SELECT 'Category' as entity, 'categories' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'slug') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'categories';

-- 8. Yarn ✅
SELECT 'Yarn' as entity, 'yarns' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'collection_id', 'brand_id', 'category_id', 'code', 'slug', 'name', 'material', 'weight', 'weight_grams', 'length', 'length_meters', 'needle_size_mm', 'composition', 'texture', 'thickness', 'features', 'pricemdl', 'priceusd', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'yarns';

-- 9. Brand ✅
SELECT 'Brand' as entity, 'brands' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'country', 'website') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'brands';

-- 10. Collection
SELECT 'Collection' as entity, 'collections' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'description', 'brand_id') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'collections';

-- 11. Attribute ✅
SELECT 'Attribute' as entity, 'attributes' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'slug', 'data_type') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'attributes';

-- 12. Order
SELECT 'Order' as entity, 'orders' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'user_id', 'shipping_address_id', 'billing_address_id', 'total_amount', 'status', 'status_id', 'comment', 'preferred_delivery_date', 'delivered_at', 'shipping_address', 'contact_phone', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'orders';

-- 13. OrderItem
SELECT 'OrderItem' as entity, 'order_items' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'color_id', 'quantity', 'unit_price', 'total_price', 'price', 'notes', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'order_items';

-- 14. Payment ✅
SELECT 'Payment' as entity, 'payments' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'status_id', 'provider', 'provider_tx_id', 'amount_mdl', 'amount_usd', 'paid_at', 'payment_method', 'currency', 'exchange_rate', 'description', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'payments';

-- 15. PaymentStatus ✅
SELECT 'PaymentStatus' as entity, 'payment_statuses' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'code', 'name') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'payment_statuses';

-- 16. PaymentStatusHistory ✅
SELECT 'PaymentStatusHistory' as entity, 'payment_status_history' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'payment_id', 'from_status_id', 'to_status_id', 'changed_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'payment_status_history';

-- 17. ContactMessage
SELECT 'ContactMessage' as entity, 'contact_messages' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'email', 'subject', 'message', 'file_path', 'read_at', 'is_read', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'contact_messages';

-- 18. MediaAsset
SELECT 'MediaAsset' as entity, 'media_assets' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'entity_type', 'entity_id', 'variant', 'format', 'url', 'width_px', 'height_px', 'size_bytes', 'alt_text', 'sort_order', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'media_assets';

-- 19. NewsletterSubscriber
SELECT 'NewsletterSubscriber' as entity, 'newsletter_subscribers' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'email', 'confirmation_token', 'user_id', 'subscribed_at', 'confirmed', 'unsubscribed') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'newsletter_subscribers';

-- 20. Pattern ✅
SELECT 'Pattern' as entity, 'patterns' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'code', 'yarn_id', 'pdf_url', 'difficulty', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'patterns';

-- 21. PatternTranslation ✅
SELECT 'PatternTranslation' as entity, 'pattern_translations' as table_name, column_name,
  CASE WHEN column_name IN ('pattern_id', 'locale', 'name', 'description') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'pattern_translations';

-- 22. Return ✅
SELECT 'Return' as entity, 'returns' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'status', 'refund_amount_czk', 'refund_amount_mdl', 'refund_amount_usd', 'reason', 'description', 'return_date', 'refund_date', 'return_method', 'tracking_number', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'returns';

-- 23. ReturnItem ✅
SELECT 'ReturnItem' as entity, 'return_items' as table_name, column_name,
  CASE WHEN column_name IN ('return_id', 'color_id', 'quantity', 'reason') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'return_items';

-- 24. ReturnStatusHistory ✅
SELECT 'ReturnStatusHistory' as entity, 'return_status_history' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'return_id', 'old_status', 'new_status', 'changed_by', 'changed_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'return_status_history';

-- 25. Shipment
SELECT 'Shipment' as entity, 'shipments' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'method_id', 'shipping_method_id', 'tracking_no', 'tracking_number', 'status', 'shipped_at', 'delivered_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'shipments';

-- 26. ShipmentStatusHistory
SELECT 'ShipmentStatusHistory' as entity, 'shipment_status_history' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'shipment_id', 'from_status', 'to_status', 'status', 'changed_at', 'changed_by') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'shipment_status_history';

-- 27. ShippingMethod
SELECT 'ShippingMethod' as entity, 'shipping_methods' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'name', 'carrier', 'service', 'price', 'base_price_czk', 'delivery_days_min', 'delivery_days_max', 'estimated_time', 'active') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'shipping_methods';

-- 28. ShippingAddress ✅
SELECT 'ShippingAddress' as entity, 'shipping_addresses' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'user_id', 'address_type', 'recipient', 'street_1', 'street_2', 'city', 'region', 'postal_code', 'country', 'phone', 'created_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'shipping_addresses';

-- 29. UserRole ✅
SELECT 'UserRole' as entity, 'user_roles' as table_name, column_name,
  CASE WHEN column_name IN ('user_id', 'role_id') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'user_roles';

-- 30. UserFavorite (исправлено: только color_id, без yarn_id)
SELECT 'UserFavorite' as entity, 'user_favorites' as table_name, column_name,
  CASE WHEN column_name IN ('user_id', 'color_id', 'added_at', 'notes', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'user_favorites';

-- 31. YarnAttributeValue ✅
SELECT 'YarnAttributeValue' as entity, 'yarn_attribute_values' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'yarn_id', 'attribute_id', 'value', 'value_text', 'value_number', 'value_bool', 'value_enum', 'created_at', 'updated_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_attribute_values';

-- 32. YarnTranslation ✅
SELECT 'YarnTranslation' as entity, 'yarn_translations' as table_name, column_name,
  CASE WHEN column_name IN ('yarn_id', 'locale', 'name', 'description') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_translations';

-- 33. YarnCategory ✅
SELECT 'YarnCategory' as entity, 'yarn_category' as table_name, column_name,
  CASE WHEN column_name IN ('yarn_id', 'category_id') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_category';

-- 34. YarnPrice ✅
SELECT 'YarnPrice' as entity, 'yarn_prices' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'yarn_id', 'price_czk', 'valid_from', 'valid_to') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'yarn_prices';

-- 35. ColorPrice ✅
SELECT 'ColorPrice' as entity, 'color_prices' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'color_id', 'price_czk', 'valid_from', 'valid_to') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'color_prices';

-- 36. OrderStatus ✅
SELECT 'OrderStatus' as entity, 'order_statuses' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'code', 'name') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'order_statuses';

-- 37. OrderStatusHistory ✅
SELECT 'OrderStatusHistory' as entity, 'order_status_history' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'from_status_id', 'to_status_id', 'changed_by', 'changed_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'order_status_history';

-- 38. OrderDiscount ✅
SELECT 'OrderDiscount' as entity, 'order_discounts' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'order_id', 'rule_id', 'amount_czk', 'applied_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'order_discounts';

-- 39. DiscountRule ✅
SELECT 'DiscountRule' as entity, 'discount_rules' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'code', 'name', 'description', 'type', 'value', 'max_uses', 'max_uses_per_user', 'min_order_total_czk', 'valid_from', 'valid_to', 'created_by', 'created_at', 'is_active') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'discount_rules';

-- 40. GuestToken ✅
SELECT 'GuestToken' as entity, 'guest_tokens' as table_name, column_name,
  CASE WHEN column_name IN ('id', 'cart_id', 'token', 'created_at', 'expires_at') THEN '✅ MATCH' ELSE '❌ EXTRA' END as status
FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'yarn_store_local' AND table_name = 'guest_tokens';

-- СЛУЖЕБНЫЕ ТАБЛИЦЫ (не требуют проверки entity)
-- 41. colors_v_legacy - legacy view
-- 42. yarn_attribute_values__bak_20250906 - backup table
-- 43. cart_items_backup - backup table
-- 44. flyway_schema_history - migration history

-- ИТОГОВАЯ СТАТИСТИКА
SELECT
  'ИТОГО' as summary,
  COUNT(*) as total_tables,
  COUNT(CASE WHEN table_name IN (
    'refresh_token', 'roles', 'users', 'carts', 'cart_items', 'colors',
    'categories', 'yarns', 'brands', 'collections', 'attributes',
    'orders', 'order_items', 'payments', 'payment_statuses', 'payment_status_history',
    'contact_messages', 'media_assets', 'newsletter_subscribers', 'patterns',
    'pattern_translations', 'returns', 'return_items', 'return_status_history',
    'shipments', 'shipment_status_history', 'shipping_methods', 'shipping_addresses',
    'user_roles', 'user_favorites', 'yarn_attribute_values', 'yarn_translations',
    'yarn_category', 'yarn_prices', 'color_prices', 'order_statuses',
    'order_status_history', 'order_discounts', 'discount_rules', 'guest_tokens'
  ) THEN 1 END) as checked_tables,
  COUNT(CASE WHEN table_name IN (
    'colors_v_legacy', 'yarn_attribute_values__bak_20250906', 'cart_items_backup', 'flyway_schema_history'
  ) THEN 1 END) as service_tables
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = 'yarn_store_local';

-- ПРОВЕРКА НА ПОЛНОТУ ПОКРЫТИЯ
SELECT
  'НЕПОКРЫТЫЕ ТАБЛИЦЫ' as check_type,
  table_name as uncovered_table
FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = 'yarn_store_local'
  AND table_name NOT IN (
    'refresh_token', 'roles', 'users', 'carts', 'cart_items', 'colors',
    'categories', 'yarns', 'brands', 'collections', 'attributes',
    'orders', 'order_items', 'payments', 'payment_statuses', 'payment_status_history',
    'contact_messages', 'media_assets', 'newsletter_subscribers', 'patterns',
    'pattern_translations', 'returns', 'return_items', 'return_status_history',
    'shipments', 'shipment_status_history', 'shipping_methods', 'shipping_addresses',
    'user_roles', 'user_favorites', 'yarn_attribute_values', 'yarn_translations',
    'yarn_category', 'yarn_prices', 'color_prices', 'order_statuses',
    'order_status_history', 'order_discounts', 'discount_rules', 'guest_tokens',
    'colors_v_legacy', 'yarn_attribute_values__bak_20250906', 'cart_items_backup', 'flyway_schema_history'
  )
ORDER BY table_name;