-- Flyway Migration: V10__manual_patch.sql
-- Description: Manual patch to sync staging/prod with yarn_store_final_2025.sql

SET FOREIGN_KEY_CHECKS=0;

-- New Table: order_status_transitions
CREATE TABLE IF NOT EXISTS order_status_transitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_status_id BIGINT,
    to_status_id BIGINT,
    allowed BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- New Table: pattern_translations
CREATE TABLE IF NOT EXISTS pattern_translations (
    pattern_id BIGINT NOT NULL,
    locale VARCHAR(10) NOT NULL,
    name VARCHAR(255),
    description TEXT,
    PRIMARY KEY (pattern_id, locale),
    FOREIGN KEY (pattern_id) REFERENCES patterns(id) ON DELETE CASCADE
);

-- New Table: color_translations
CREATE TABLE IF NOT EXISTS color_translations (
    color_id BIGINT NOT NULL,
    locale VARCHAR(10) NOT NULL,
    name VARCHAR(100),
    PRIMARY KEY (color_id, locale),
    FOREIGN KEY (color_id) REFERENCES colors(id) ON DELETE CASCADE
);

-- New Table: attribute_translations
CREATE TABLE IF NOT EXISTS attribute_translations (
    attribute_id BIGINT NOT NULL,
    locale VARCHAR(10) NOT NULL,
    name VARCHAR(100),
    PRIMARY KEY (attribute_id, locale),
    FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE
);

-- Alter colors
ALTER TABLE colors ADD COLUMN IF NOT EXISTS slug VARCHAR(128);
ALTER TABLE colors ADD COLUMN IF NOT EXISTS locale VARCHAR(10);
ALTER TABLE colors ADD COLUMN IF NOT EXISTS name VARCHAR(100);

-- Alter patterns
ALTER TABLE patterns ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE patterns ADD COLUMN IF NOT EXISTS slug VARCHAR(128);
ALTER TABLE patterns ADD COLUMN IF NOT EXISTS locale VARCHAR(10);
ALTER TABLE patterns ADD COLUMN IF NOT EXISTS translated BOOLEAN DEFAULT FALSE;

-- Alter cart_items
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS pattern_id BIGINT;
ALTER TABLE cart_items ADD COLUMN IF NOT EXISTS color_id BIGINT;

-- Add foreign keys to cart_items
ALTER TABLE cart_items ADD CONSTRAINT fk_cartitem_pattern FOREIGN KEY (pattern_id) REFERENCES patterns(id) ON DELETE SET NULL;
ALTER TABLE cart_items ADD CONSTRAINT fk_cartitem_color FOREIGN KEY (color_id) REFERENCES colors(id) ON DELETE SET NULL;

-- Insert order statuses if not exist
INSERT IGNORE INTO order_statuses (id, name) VALUES
    (1, 'NEW'),
    (2, 'PROCESSING'),
    (3, 'SHIPPED'),
    (4, 'DELIVERED'),
    (5, 'CANCELLED');

SET FOREIGN_KEY_CHECKS=1;