-- ===== SAFELY REWIRE attr_id -> attribute_id =====
SET @OLD_FK = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;
SET @DB := DATABASE();

-- 1) Снять ВСЕ внешние ключи, которые цепляются за колонку attr_id
SELECT GROUP_CONCAT(CONCAT('ALTER TABLE `yarn_attribute_values` DROP FOREIGN KEY `', kcu.CONSTRAINT_NAME, '`') SEPARATOR ';')
INTO @drop_fk_sql
FROM information_schema.KEY_COLUMN_USAGE kcu
WHERE kcu.TABLE_SCHEMA = @DB
  AND kcu.TABLE_NAME = 'yarn_attribute_values'
  AND kcu.COLUMN_NAME = 'attr_id'
  AND kcu.REFERENCED_TABLE_NAME IS NOT NULL;

SET @drop_fk_sql = IFNULL(@drop_fk_sql, 'DO 0');
PREPARE s FROM @drop_fk_sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 2) Привести колонку: если есть только attr_id → переименуем;
--    если есть обе → удалим старую attr_id
SET @has_attr := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attr_id'
);
SET @has_attribute := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attribute_id'
);

-- case A: есть attr_id и НЕТ attribute_id → переименуем
SET @sql := IF(@has_attr=1 AND @has_attribute=0,
  'ALTER TABLE yarn_attribute_values CHANGE COLUMN attr_id attribute_id BIGINT NOT NULL',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- case B: есть обе → удалим attr_id
SET @sql := IF(@has_attr=1 AND @has_attribute=1,
  'ALTER TABLE yarn_attribute_values DROP COLUMN attr_id',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 3) Гарантируем id PK и value
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS id BIGINT AUTO_INCREMENT FIRST;

-- Если PK не по id — переставим
SET @pk_is_id := (
  SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND CONSTRAINT_NAME='PRIMARY' AND COLUMN_NAME='id'
);
SET @has_pk := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND CONSTRAINT_TYPE='PRIMARY KEY'
);

SET @sql := IF(@has_pk=1 AND @pk_is_id=0, 'ALTER TABLE yarn_attribute_values DROP PRIMARY KEY', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF(@pk_is_id=0, 'ALTER TABLE yarn_attribute_values ADD PRIMARY KEY (id)', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS value VARCHAR(255) NOT NULL DEFAULT '' AFTER attribute_id;

-- 4) Индексы
CREATE INDEX IF NOT EXISTS idx_yarn_attr_yarn_id ON yarn_attribute_values(yarn_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attr_attr_id ON yarn_attribute_values(attribute_id);

-- Уникальность пары, если её ещё нет
SET @has_unique := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND INDEX_NAME='unique_yarn_attribute' AND NON_UNIQUE=0
);
SET @sql := IF(@has_unique=0,
  'ALTER TABLE yarn_attribute_values ADD UNIQUE KEY unique_yarn_attribute (yarn_id, attribute_id)',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 5) Вернуть внешний ключ уже на attribute_id → attributes(id)
--    (в исходной схеме delete был CASCADE; если нужно — поменяй правило)
SET @fk_exists := (
  SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND COLUMN_NAME='attribute_id' AND REFERENCED_TABLE_NAME='attributes'
);
SET @sql := IF(@fk_exists=0,
  'ALTER TABLE yarn_attribute_values ADD CONSTRAINT fk_yav_attribute FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET FOREIGN_KEY_CHECKS = @OLD_FK;

SELECT '✅ attr_id → attribute_id: FK rewired & column fixed' AS result, NOW() AS done_at;
