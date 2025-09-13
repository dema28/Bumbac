-- Flyway Migration: V9__attributes_and_colors.sql
-- Generated: 2025-09-03 19:45:16 (FIXED)
-- Description: Inserts attributes, yarn colors, and variant prices

SET FOREIGN_KEY_CHECKS=0;

-- ATTRIBUTES (правильная структура: id, name, slug, data_type)
INSERT IGNORE INTO attributes (id, name, slug, data_type) VALUES
(1, 'Length', 'length', 'NUMBER'),
(2, 'Weight', 'weight', 'NUMBER'),
(3, 'Needle Size', 'needle-size', 'TEXT'),
(4, 'Composition', 'composition', 'TEXT'),
(5, 'Thickness', 'thickness', 'ENUM'),
(6, 'Texture', 'texture', 'ENUM'),
(7, 'Machine Washable', 'machine-washable', 'BOOLEAN'),
(8, 'Hypoallergenic', 'hypoallergenic', 'BOOLEAN');

-- YARN_ATTRIBUTE_VALUES (правильная структура: yarn_id, attr_id, value_text, value_number, value_bool, value_enum)
-- Пряжа 1 (Wool)
INSERT IGNORE INTO yarn_attribute_values (yarn_id, attr_id, value_number, value_text, value_bool, value_enum) VALUES
(1, 1, 400.00, NULL, NULL, NULL),        -- Length: 400m
(1, 2, 100.00, NULL, NULL, NULL),        -- Weight: 100g
(1, 3, NULL, '4-5 mm', NULL, NULL),      -- Needle Size: 4-5 mm
(1, 4, NULL, '100% Wool', NULL, NULL),   -- Composition: 100% Wool
(1, 5, NULL, NULL, NULL, 'Medium'),      -- Thickness: Medium
(1, 6, NULL, NULL, NULL, 'Smooth'),      -- Texture: Smooth
(1, 7, NULL, NULL, 0, NULL),             -- Machine Washable: false
(1, 8, NULL, NULL, 1, NULL);             -- Hypoallergenic: true

-- Пряжа 2 (Cotton)
INSERT IGNORE INTO yarn_attribute_values (yarn_id, attr_id, value_number, value_text, value_bool, value_enum) VALUES
(2, 1, 350.00, NULL, NULL, NULL),        -- Length: 350m
(2, 2, 100.00, NULL, NULL, NULL),        -- Weight: 100g
(2, 3, NULL, '3.5-4 mm', NULL, NULL),    -- Needle Size: 3.5-4 mm
(2, 4, NULL, '100% Cotton', NULL, NULL), -- Composition: 100% Cotton
(2, 5, NULL, NULL, NULL, 'Fine'),        -- Thickness: Fine
(2, 6, NULL, NULL, NULL, 'Soft'),        -- Texture: Soft
(2, 7, NULL, NULL, 1, NULL),             -- Machine Washable: true
(2, 8, NULL, NULL, 1, NULL);             -- Hypoallergenic: true

-- Пряжа 3 (Baby Yarn)
INSERT IGNORE INTO yarn_attribute_values (yarn_id, attr_id, value_number, value_text, value_bool, value_enum) VALUES
(3, 1, 200.00, NULL, NULL, NULL),        -- Length: 200m
(3, 2, 50.00, NULL, NULL, NULL),         -- Weight: 50g
(3, 3, NULL, '2.5-3 mm', NULL, NULL),    -- Needle Size: 2.5-3 mm
(3, 4, NULL, '100% Baby Acrylic', NULL, NULL), -- Composition
(3, 5, NULL, NULL, NULL, 'Extra Fine'),  -- Thickness: Extra Fine
(3, 6, NULL, NULL, NULL, 'Ultra Soft'),  -- Texture: Ultra Soft
(3, 7, NULL, NULL, 1, NULL),             -- Machine Washable: true
(3, 8, NULL, NULL, 1, NULL);             -- Hypoallergenic: true

-- Пряжа 4 (Chunky Wool)
INSERT IGNORE INTO yarn_attribute_values (yarn_id, attr_id, value_number, value_text, value_bool, value_enum) VALUES
(4, 1, 120.00, NULL, NULL, NULL),        -- Length: 120m
(4, 2, 150.00, NULL, NULL, NULL),        -- Weight: 150g
(4, 3, NULL, '8-10 mm', NULL, NULL),     -- Needle Size: 8-10 mm
(4, 4, NULL, '80% Wool, 20% Acrylic', NULL, NULL), -- Composition
(4, 5, NULL, NULL, NULL, 'Chunky'),      -- Thickness: Chunky
(4, 6, NULL, NULL, NULL, 'Textured'),    -- Texture: Textured
(4, 7, NULL, NULL, 0, NULL),             -- Machine Washable: false
(4, 8, NULL, NULL, 0, NULL);             -- Hypoallergenic: false

-- Пряжа 5 (Acrylic)
INSERT IGNORE INTO yarn_attribute_values (yarn_id, attr_id, value_number, value_text, value_bool, value_enum) VALUES
(5, 1, 380.00, NULL, NULL, NULL),        -- Length: 380m
(5, 2, 100.00, NULL, NULL, NULL),        -- Weight: 100g
(5, 3, NULL, '4 mm', NULL, NULL),        -- Needle Size: 4 mm
(5, 4, NULL, '100% Acrylic', NULL, NULL), -- Composition: 100% Acrylic
(5, 5, NULL, NULL, NULL, 'Medium'),      -- Thickness: Medium
(5, 6, NULL, NULL, NULL, 'Smooth'),      -- Texture: Smooth
(5, 7, NULL, NULL, 1, NULL),             -- Machine Washable: true
(5, 8, NULL, NULL, 1, NULL);             -- Hypoallergenic: true

-- ДОПОЛНИТЕЛЬНЫЕ ЦВЕТА для пряжи (colors уже существует, добавляем больше вариантов)
INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (6, 1, 'C002', 'Black', 5.99, 85, '#000000', 'YARN-1-BLACK');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (7, 1, 'C003', 'Grey', 5.99, 92, '#808080', 'YARN-1-GREY');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (8, 2, 'C002', 'Navy Blue', 6.99, 67, '#000080', 'YARN-2-NAVY');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (9, 2, 'C003', 'Pink', 6.99, 78, '#FFC0CB', 'YARN-2-PINK');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (10, 3, 'C002', 'Baby Blue', 7.99, 45, '#ADD8E6', 'YARN-3-BABY-BLUE');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (11, 4, 'C002', 'Charcoal', 8.99, 23, '#36454F', 'YARN-4-CHARCOAL');

INSERT IGNORE INTO colors (id, yarn_id, color_code, color_name, price, stock_quantity, hex_value, sku)
VALUES (12, 5, 'C002', 'Royal Blue', 9.99, 56, '#4169E1', 'YARN-5-ROYAL');

-- В вашей системе используются MDL и USD, но базовые таблицы price имеют CZK
-- Оставляем CZK в базовых таблицах (color_prices, yarn_prices) как техническое поле
-- Приложение работает с priceMDL и priceUSD из таблицы yarns и colors.price

-- ЦЕНЫ НА ЦВЕТА (в CZK для совместимости с базовой схемой, но приложение использует price из colors)
INSERT IGNORE INTO color_prices (id, color_id, price_czk, valid_from, valid_to)
VALUES (2, 2, 140.00, '2025-08-01', '9999-12-31');

INSERT IGNORE INTO color_prices (id, color_id, price_czk, valid_from, valid_to)
VALUES (3, 3, 160.00, '2025-08-01', '9999-12-31');

INSERT IGNORE INTO color_prices (id, color_id, price_czk, valid_from, valid_to)
VALUES (4, 4, 180.00, '2025-08-01', '9999-12-31');

INSERT IGNORE INTO color_prices (id, color_id, price_czk, valid_from, valid_to)
VALUES (5, 5, 200.00, '2025-08-01', '9999-12-31');

-- ОБНОВЛЕНИЕ ЦЕН В ОСНОВНОЙ ВАЛЮТЕ (colors.price содержит актуальную цену в MDL)
UPDATE colors SET price = 6.99 WHERE id = 2;
UPDATE colors SET price = 7.99 WHERE id = 3;
UPDATE colors SET price = 8.99 WHERE id = 4;
UPDATE colors SET price = 9.99 WHERE id = 5;

-- ОБНОВЛЕНИЕ ЦЕН ПРЯЖИ (yarns.pricemdl и yarns.priceusd - основные валюты)
UPDATE yarns SET pricemdl = 6.50, priceusd = 0.34 WHERE id = 2;
UPDATE yarns SET pricemdl = 7.50, priceusd = 0.39 WHERE id = 3;
UPDATE yarns SET pricemdl = 8.50, priceusd = 0.44 WHERE id = 4;
UPDATE yarns SET pricemdl = 9.50, priceusd = 0.49 WHERE id = 5;

SET FOREIGN_KEY_CHECKS=1;