-- Flyway Migration: V6__patterns_and_translations.sql
-- Generated: 2025-09-03 19:44:22 (FIXED)
-- Description: Inserts test patterns and their translations

SET FOREIGN_KEY_CHECKS=0;

-- PATTERNS (используем правильную структуру из схемы)
-- Реальная структура: id, code, yarn_id, pdf_url, difficulty, created_at, updated_at
INSERT IGNORE INTO patterns (id, code, yarn_id, pdf_url, difficulty, created_at, updated_at)
VALUES (1001, 'PATTERN-001', 1, '/media/patterns/pattern_1.pdf', 'BEGINNER', NOW(), NOW());

INSERT IGNORE INTO patterns (id, code, yarn_id, pdf_url, difficulty, created_at, updated_at)
VALUES (1002, 'PATTERN-002', 2, '/media/patterns/pattern_2.pdf', 'INTERMEDIATE', NOW(), NOW());

INSERT IGNORE INTO patterns (id, code, yarn_id, pdf_url, difficulty, created_at, updated_at)
VALUES (1003, 'PATTERN-003', 3, '/media/patterns/pattern_3.pdf', 'ADVANCED', NOW(), NOW());

INSERT IGNORE INTO patterns (id, code, yarn_id, pdf_url, difficulty, created_at, updated_at)
VALUES (1004, 'PATTERN-004', 4, '/media/patterns/pattern_4.pdf', 'BEGINNER', NOW(), NOW());

INSERT IGNORE INTO patterns (id, code, yarn_id, pdf_url, difficulty, created_at, updated_at)
VALUES (1005, 'PATTERN-005', NULL, '/media/patterns/pattern_5.pdf', 'INTERMEDIATE', NOW(), NOW());

-- PATTERN_TRANSLATIONS
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1001, 'en', 'Beginner Scarf Pattern', 'A simple scarf pattern perfect for beginners');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1001, 'ru', 'Схема шарфа для начинающих', 'Простая схема шарфа, идеальная для новичков');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1001, 'ro', 'Model de fular pentru începători', 'Un model simplu de fular perfect pentru începători');

INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1002, 'en', 'Cotton Baby Hat', 'Cute baby hat made with cotton yarn');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1002, 'ru', 'Детская шапочка из хлопка', 'Милая детская шапочка из хлопковой пряжи');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1002, 'ro', 'Căciulă de bebeluș din bumbac', 'Căciulă drăguță pentru bebeluși din fire de bumbac');

INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1003, 'en', 'Advanced Chunky Sweater', 'Complex sweater pattern for experienced knitters');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1003, 'ru', 'Сложный свитер из толстой пряжи', 'Сложная схема свитера для опытных вязальщиц');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1003, 'ro', 'Pulover complex din fire groase', 'Model complex de pulover pentru tricotoarele experimentate');

INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1004, 'en', 'Simple Mittens', 'Basic mitten pattern for cold weather');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1004, 'ru', 'Простые варежки', 'Базовая схема варежек для холодной погоды');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1004, 'ro', 'Mănuși simple', 'Model de bază de mănuși pentru vremea rece');

INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1005, 'en', 'Universal Dishcloth', 'Multi-purpose dishcloth pattern');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1005, 'ru', 'Универсальная мочалка', 'Многофункциональная схема мочалки');
INSERT IGNORE INTO pattern_translations (pattern_id, locale, name, description)
VALUES (1005, 'ro', 'Cârpă universală', 'Model multifuncțional de cârpă de bucătărie');

-- MEDIA_ASSETS для схем (PDF файлы)
INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url, alt_text, sort_order, created_at)
VALUES (101, 'PATTERN', 1001, 'ORIGINAL', 'PDF', '/media/patterns/pattern-001-scarf.pdf', 'Схема шарфа для начинающих', 1, NOW());

INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url, alt_text, sort_order, created_at)
VALUES (102, 'PATTERN', 1002, 'ORIGINAL', 'PDF', '/media/patterns/pattern-002-baby-hat.pdf', 'Схема детской шапочки', 1, NOW());

INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url, alt_text, sort_order, created_at)
VALUES (103, 'PATTERN', 1003, 'ORIGINAL', 'PDF', '/media/patterns/pattern-003-chunky-sweater.pdf', 'Схема свитера из толстой пряжи', 1, NOW());

INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url, alt_text, sort_order, created_at)
VALUES (104, 'PATTERN', 1004, 'ORIGINAL', 'JPEG', '/media/patterns/pattern-004-mittens-preview.jpg', 'Превью схемы варежек', 1, NOW());

INSERT IGNORE INTO media_assets (id, entity_type, entity_id, variant, format, url, alt_text, sort_order, created_at)
VALUES (105, 'PATTERN', 1005, 'ORIGINAL', 'PDF', '/media/patterns/pattern-005-dishcloth.pdf', 'Схема универсальной мочалки', 1, NOW());

SET FOREIGN_KEY_CHECKS=1;