-- Flyway Migration: V3__test_seed_data.sql
-- Generated: 2025-09-03 19:35:18 (FIXED)
-- Description: Inserts test data into brands, categories, collections, yarns, translations and related tables
-- ВАЖНО: Для локальной базы yarn_store_local

SET FOREIGN_KEY_CHECKS=0;

-- BRANDS (сначала бренды, так как коллекции ссылаются на них)
INSERT IGNORE INTO brands (id, name) VALUES (1, 'Alize');
INSERT IGNORE INTO brands (id, name) VALUES (2, 'YarnArt');
INSERT IGNORE INTO brands (id, name) VALUES (3, 'Himalaya');
INSERT IGNORE INTO brands (id, name) VALUES (4, 'Etrofil');
INSERT IGNORE INTO brands (id, name) VALUES (5, 'Madame Tricote');

-- CATEGORIES
INSERT IGNORE INTO categories (id, name, slug) VALUES (1, 'Acrylic', 'acrylic');
INSERT IGNORE INTO categories (id, name, slug) VALUES (2, 'Wool', 'wool');
INSERT IGNORE INTO categories (id, name, slug) VALUES (3, 'Cotton', 'cotton');
INSERT IGNORE INTO categories (id, name, slug) VALUES (4, 'Baby', 'baby');
INSERT IGNORE INTO categories (id, name, slug) VALUES (5, 'Chunky', 'chunky');

-- COLLECTIONS (теперь можно вставить, так как бренды уже есть)
INSERT IGNORE INTO collections (id, brand_id, name) VALUES (1, 1, 'Winter 2025');
INSERT IGNORE INTO collections (id, brand_id, name) VALUES (2, 2, 'Baby Soft');
INSERT IGNORE INTO collections (id, brand_id, name) VALUES (3, 3, 'Ombre Series');
INSERT IGNORE INTO collections (id, brand_id, name) VALUES (4, 4, 'Multicolor Line');

-- MEDIA_ASSETS (вместо таблицы media)
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url)
VALUES (1, 'YARN', 1, 'ORIGINAL', 'JPEG', '/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-8027-.jpg');
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url)
VALUES (2, 'YARN', 2, 'ORIGINAL', 'JPEG', '/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7914-.jpg');
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url)
VALUES (3, 'YARN', 3, 'ORIGINAL', 'JPEG', '/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7804-2.jpg');
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url)
VALUES (4, 'YARN', 4, 'ORIGINAL', 'JPEG', '/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7786-2.jpg');
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url)
VALUES (5, 'YARN', 5, 'ORIGINAL', 'JPEG', '/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7785-2.jpg');

-- YARNS (с правильной структурой согласно схеме)
INSERT IGNORE INTO yarns (id, brand_id, category_id, collection_id, code, slug, weight_grams, length_meters, material, pricemdl, priceusd)
VALUES (1, 2, 2, 2, 'TEST-001', 'test-yarn-1', 100.00, 400.00, 'Wool', 5.99, 0.25);

INSERT IGNORE INTO yarns (id, brand_id, category_id, collection_id, code, slug, weight_grams, length_meters, material, pricemdl, priceusd, created_at, updated_at)
VALUES (2, 3, 3, 3, 'TEST-002', 'test-yarn-2', 100.00, 350.00, 'Cotton', 6.99, 0.29, NOW(), NOW());

INSERT IGNORE INTO yarns (id, brand_id, category_id, collection_id, code, slug, weight_grams, length_meters, material, pricemdl, priceusd, created_at, updated_at)
VALUES (3, 4, 4, 4, 'TEST-003', 'test-yarn-3', 50.00, 200.00, 'Baby Yarn', 7.99, 0.33, NOW(), NOW());

INSERT IGNORE INTO yarns (id, brand_id, category_id, collection_id, code, slug, weight_grams, length_meters, material, pricemdl, priceusd, created_at, updated_at)
VALUES (4, 5, 5, 1, 'TEST-004', 'test-yarn-4', 150.00, 120.00, 'Chunky Wool', 8.99, 0.37, NOW(), NOW());

INSERT IGNORE INTO yarns (id, brand_id, category_id, collection_id, code, slug, weight_grams, length_meters, material, pricemdl, priceusd, created_at, updated_at)
VALUES (5, 1, 1, 2, 'TEST-005', 'test-yarn-5', 100.00, 380.00, 'Acrylic', 9.99, 0.41, NOW(), NOW());

-- YARN_TRANSLATIONS
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (1, 'en', 'Test Yarn 1', 'English description for yarn 1');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (1, 'ru', 'Тестовая пряжа 1', 'Описание на русском для пряжи 1');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (1, 'ro', 'Fir de test 1', 'Descriere în română pentru firul 1');

INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (2, 'en', 'Test Yarn 2', 'English description for yarn 2');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (2, 'ru', 'Тестовая пряжа 2', 'Описание на русском для пряжи 2');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (2, 'ro', 'Fir de test 2', 'Descriere în română pentru firul 2');

INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (3, 'en', 'Test Yarn 3', 'English description for yarn 3');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (3, 'ru', 'Тестовая пряжа 3', 'Описание на русском для пряжи 3');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (3, 'ro', 'Fir de test 3', 'Descriere în română pentru firul 3');

INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (4, 'en', 'Test Yarn 4', 'English description for yarn 4');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (4, 'ru', 'Тестовая пряжа 4', 'Описание на русском для пряжи 4');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (4, 'ro', 'Fir de test 4', 'Descriere în română pentru firul 4');

INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (5, 'en', 'Test Yarn 5', 'English description for yarn 5');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (5, 'ru', 'Тестовая пряжа 5', 'Описание на русском для пряжи 5');
INSERT IGNORE INTO yarn_translations (yarn_id, locale, name, description)
VALUES (5, 'ro', 'Fir de test 5', 'Descriere în română pentru firul 5');

-- COLORS (если нужны тестовые цвета)
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price)
VALUES (1, 1, 'C001', 'White', 5.99);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price)
VALUES (2, 1, 'C002', 'Black', 5.99);
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price)
VALUES (3, 2, 'C001', 'Red', 6.99);

SET FOREIGN_KEY_CHECKS=1;