-- ===== Robust fix for yarn_attribute_values: attr_id→attribute_id + fresh PK(id) =====
SET @OLD_FK = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;
SET @DB := DATABASE();

-- 0) Снять ВСЕ FK из этой таблицы (исходящие), чтобы ничего не мешало
SELECT GROUP_CONCAT(CONCAT('ALTER TABLE `yarn_attribute_values` DROP FOREIGN KEY `', kcu.CONSTRAINT_NAME, '`') SEPARATOR ';')
INTO @drop_fk_sql
FROM information_schema.KEY_COLUMN_USAGE kcu
WHERE kcu.TABLE_SCHEMA = @DB
  AND kcu.TABLE_NAME = 'yarn_attribute_values'
  AND kcu.REFERENCED_TABLE_NAME IS NOT NULL;
SET @drop_fk_sql = IFNULL(@drop_fk_sql, 'DO 0');
PREPARE s FROM @drop_fk_sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 1) Приводим колонки: attr_id -> attribute_id
SET @has_attr := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attr_id'
);
SET @has_attribute := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='attribute_id'
);

SET @sql := IF(@has_attr=1 AND @has_attribute=0,
  'ALTER TABLE yarn_attribute_values CHANGE COLUMN attr_id attribute_id BIGINT NOT NULL',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF(@has_attr=1 AND @has_attribute=1,
  'ALTER TABLE yarn_attribute_values DROP COLUMN attr_id',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 2) value, если нет
ALTER TABLE yarn_attribute_values
  ADD COLUMN IF NOT EXISTS value VARCHAR(255) NOT NULL DEFAULT '' AFTER attribute_id;

-- 3) убрать уникальный индекс пары (если есть), чтобы не мешал переклейке PK
SET @has_uniq := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values'
    AND INDEX_NAME='unique_yarn_attribute' AND NON_UNIQUE=0
);
SET @sql := IF(@has_uniq=1,
  'ALTER TABLE yarn_attribute_values DROP INDEX unique_yarn_attribute',
  'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 4) если есть tmp_id от прошлых попыток — удалим
SET @has_tmp := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='tmp_id'
);
SET @sql := IF(@has_tmp=1, 'ALTER TABLE yarn_attribute_values DROP COLUMN tmp_id', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 5) снять PRIMARY KEY, если есть
SET @has_pk := (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND CONSTRAINT_TYPE='PRIMARY KEY'
);
SET @sql := IF(@has_pk=1, 'ALTER TABLE yarn_attribute_values DROP PRIMARY KEY', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 6) создать свежий автоинкрементный PK (tmp_id)
ALTER TABLE yarn_attribute_values
  ADD COLUMN tmp_id BIGINT NOT NULL AUTO_INCREMENT FIRST,
  ADD PRIMARY KEY (tmp_id);

-- 7) удалить старый id, если был
SET @has_old_id := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME='yarn_attribute_values' AND COLUMN_NAME='id'
);
SET @sql := IF(@has_old_id=1, 'ALTER TABLE yarn_attribute_values DROP COLUMN id', 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 8) переименовать tmp_id -> id (с сохранением AI и PK)
ALTER TABLE yarn_attribute_values
  CHANGE COLUMN tmp_id id BIGINT NOT NULL AUTO_INCREMENT;

-- 9) вернуть индексы/уникальность и FK
CREATE INDEX IF NOT EXISTS idx_yarn_attr_yarn_id ON yarn_attribute_values(yarn_id);
CREATE INDEX IF NOT EXISTS idx_yarn_attr_attr_id ON yarn_attribute_values(attribute_id);

ALTER TABLE yarn_attribute_values
  ADD UNIQUE KEY unique_yarn_attribute (yarn_id, attribute_id);

ALTER TABLE yarn_attribute_values
  ADD CONSTRAINT fk_yav_attribute
  FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = @OLD_FK;

SELECT '✅ yarn_attribute_values repaired: attribute_id OK, id AI PK rebuilt' AS result, NOW() AS done_at;
