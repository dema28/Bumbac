-- ===== SAFELY FIX PK & FKs FOR yarn_attribute_values =====
SET @OLD_FK = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;
SET @DB := DATABASE();

-- 0) Снять ВСЕ FK, ссылающиеся на attr_id/attribute_id в этой таблице
SELECT GROUP_CONCAT(CONCAT('ALTER TABLE `yarn_attribute_values` DROP FOREIGN KEY `', kcu.CONSTRAINT_NAME, '`') SEPARATOR ';')
INTO @drop_fk_sql
FROM information_schema.KEY_COLUMN_USAGE kcu
WHERE kcu.TABLE_SCHEMA = @DB
  AND kcu.TABLE_NAME = 'yarn_attribute_values'
  AND kcu.REFERENCED_TABLE_NAME IS NOT NULL;

SET @drop_fk_sql = IFNULL(@drop_fk_sql, 'DO 0');
PREPARE s FROM @drop_fk_sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1) Приводим attr_id → attribute_id
SET @has_attr := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attr_id'
);
SET @has_attribute := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attribute_id'
);

-- 1a: если есть только attr_id → переименуем
SET @sql := IF(@has_attr=1 AND @has_attribute=0,
  'ALTER TABLE yarn_attribute_values CHANGE COLUMN attr_id attribute_id BIGINT NOT NULL',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1b: если есть обе → удалим старую attr_id
SET @sql := IF(@has_attr=1 AND @has_attribute=1,
  'ALTER TABLE yarn_attribute_values DROP COLUMN attr_id',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 2) Убедимся, что есть колонка id и она допускает NULL (чтобы перезаписать)
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS id BIGINT NULL FIRST;

-- Если колонка id была NOT NULL/AUTO_INCREMENT — снимем ограничения временно
SET @id_is_notnull := (
  SELECT CASE WHEN IS_NULLABLE='NO' THEN 1 ELSE 0 END
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='id'
  LIMIT 1
);
SET @sql := IF(@id_is_notnull=1, 'ALTER TABLE yarn_attribute_values MODIFY id BIGINT NULL', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 3) Снимем текущий PRIMARY KEY (обычно по (yarn_id, attribute_id))
SET @has_pk := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND CONSTRAINT_TYPE='PRIMARY KEY'
);
SET @sql := IF(@has_pk=1, 'ALTER TABLE yarn_attribute_values DROP PRIMARY KEY', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 4) Переинициализируем id уникальными значениями
SET @i := 0;
-- Установим NULL, чтобы не мешали дубликаты, затем присвоим последовательность
UPDATE yarn_attribute_values SET id = NULL;
UPDATE yarn_attribute_values
SET id = (@i := @i + 1)
ORDER BY yarn_id, attribute_id;

-- 5) Навесим PRIMARY KEY по id и вернем NOT NULL + AUTO_INCREMENT
ALTER TABLE yarn_attribute_values
  ADD PRIMARY KEY (id);

ALTER TABLE yarn_attribute_values
  MODIFY id BIGINT NOT NULL;

-- Сделать автоинкремент (после заполнения и PK)
ALTER TABLE yarn_attribute_values
  MODIFY id BIGINT NOT NULL AUTO_INCREMENT;

-- 6) Колонка value (если вдруг нет)
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS value VARCHAR(255) NOT NULL DEFAULT '' AFTER attribute_id;

-- 7) Индексы и уникальность пары (yarn_id, attribute_id)
CREATE INDEX IF NOT EXISTS idx_yarn_attr_yarn_id ON yarn_attribute_values(yarn_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attr_attr_id ON yarn_attribute_values(attribute_id);

SET @has_unique := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND INDEX_NAME='unique_yarn_attribute' AND NON_UNIQUE=0
);
SET @sql := IF(@has_unique=0,
  'ALTER TABLE yarn_attribute_values ADD UNIQUE KEY unique_yarn_attribute (yarn_id, attribute_id)',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 8) Вернем внешний ключ на attributes(id) уже для attribute_id
-- (поставил ON DELETE CASCADE как обычно было; при необходимости измени правило)
ALTER TABLE yarn_attribute_values
  ADD CONSTRAINT fk_yav_attribute
  FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = @OLD_FK;

SELECT '✅ yarn_attribute_values: PK re-seeded, FK rewired, structure OK' AS result, NOW() AS done_at;
