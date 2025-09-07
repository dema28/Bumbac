-- ===== Hard Rebuild for yarn_attribute_values (safe, idempotent-ish) =====
SET @OLD_FK := @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;
SET @DB := DATABASE();

-- 0) Имена для swap
SET @old := 'yarn_attribute_values';
SET @bak := 'yarn_attribute_values__bak_20250906';
SET @new := 'yarn_attribute_values__new';

-- 1) Снять все FK ИЗ таблицы (исходящие)
SELECT GROUP_CONCAT(CONCAT('ALTER TABLE `', @old, '` DROP FOREIGN KEY `', kcu.CONSTRAINT_NAME, '`') SEPARATOR ';')
INTO @drop_fk_sql
FROM information_schema.KEY_COLUMN_USAGE kcu
WHERE kcu.TABLE_SCHEMA = @DB
  AND kcu.TABLE_NAME   = @old
  AND kcu.REFERENCED_TABLE_NAME IS NOT NULL;

SET @drop_fk_sql = IFNULL(@drop_fk_sql, 'DO 0');
PREPARE s FROM @drop_fk_sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 2) Если есть старая «new»/«bak» — подчистим
SET @has_new := (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA=@DB AND TABLE_NAME=@new);
SET @has_bak := (SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA=@DB AND TABLE_NAME=@bak);

SET @sql := IF(@has_new=1, CONCAT('DROP TABLE `', @new, '`'), 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 3) Создаём НОВУЮ таблицу с целевой схемой
SET @create_new := CONCAT('
  CREATE TABLE `', @new, '` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `yarn_id` BIGINT NOT NULL,
    `attribute_id` BIGINT NOT NULL,
    `value` VARCHAR(255) NOT NULL DEFAULT '''',
    `created_at` TIMESTAMP NULL DEFAULT NULL,
    `updated_at` TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_yarn_attribute` (`yarn_id`,`attribute_id`),
    KEY `idx_yarn_attr_yarn_id` (`yarn_id`),
    KEY `idx_yarn_attr_attr_id` (`attribute_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
');
PREPARE s FROM @create_new; EXECUTE s; DEALLOCATE PREPARE s;

-- 4) Определим, как звался столбец атрибута в старой таблице (attr_id или attribute_id)
SET @has_attr := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME=@old AND COLUMN_NAME='attr_id'
);
SET @has_attribute := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME=@old AND COLUMN_NAME='attribute_id'
);

-- 5) Перенос ДАННЫХ (только уникальные пары); берём value если есть, иначе ''
-- проверка, есть ли колонка `value`
SET @has_value := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA=@DB AND TABLE_NAME=@old AND COLUMN_NAME='value'
);

-- формируем вставку в зависимости от attr_id / attribute_id / value
SET @insert_sql := IF(@has_attribute=1,
  IF(@has_value=1,
    CONCAT('INSERT INTO `', @new, '` (yarn_id, attribute_id, value, created_at, updated_at)
            SELECT y.yarn_id,
                   y.attribute_id,
                   COALESCE(y.value, ''''),
                   NULL, NULL
            FROM `', @old, '` y
            GROUP BY y.yarn_id, y.attribute_id'),
    CONCAT('INSERT INTO `', @new, '` (yarn_id, attribute_id, value, created_at, updated_at)
            SELECT y.yarn_id,
                   y.attribute_id,
                   '''',
                   NULL, NULL
            FROM `', @old, '` y
            GROUP BY y.yarn_id, y.attribute_id')
  ),
  IF(@has_attr=1,
    IF(@has_value=1,
      CONCAT('INSERT INTO `', @new, '` (yarn_id, attribute_id, value, created_at, updated_at)
              SELECT y.yarn_id,
                     y.attr_id AS attribute_id,
                     COALESCE(y.value, ''''),
                     NULL, NULL
              FROM `', @old, '` y
              GROUP BY y.yarn_id, attribute_id'),
      CONCAT('INSERT INTO `', @new, '` (yarn_id, attribute_id, value, created_at, updated_at)
              SELECT y.yarn_id,
                     y.attr_id AS attribute_id,
                     '''',
                     NULL, NULL
              FROM `', @old, '` y
              GROUP BY y.yarn_id, attribute_id')
  ),
  'DO 0')
);


-- 6) Переименуем старую таблицу в backup и новую в боевую
-- если бэкап уже есть — удалим (чтобы RENAME прошёл)
SET @sql := IF(@has_bak=1, CONCAT('DROP TABLE `', @bak, '`'), 'DO 0');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @rename := CONCAT('RENAME TABLE `', @old, '` TO `', @bak, '`, `', @new, '` TO `', @old, '`;');
PREPARE s FROM @rename; EXECUTE s; DEALLOCATE PREPARE s;

-- 7) Вернём ВНЕШНИЕ КЛЮЧИ (минимум два: attribute_id -> attributes.id, yarn_id -> yarns.id)
SET @add_fk1 := CONCAT('ALTER TABLE `', @old, '` ADD CONSTRAINT `fk_yav_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes`(`id`) ON DELETE CASCADE');
SET @add_fk2 := CONCAT('ALTER TABLE `', @old, '` ADD CONSTRAINT `fk_yav_yarn`      FOREIGN KEY (`yarn_id`)      REFERENCES `yarns`(`id`)      ON DELETE CASCADE');

PREPARE s FROM @add_fk1; EXECUTE s; DEALLOCATE PREPARE s;
PREPARE s FROM @add_fk2; EXECUTE s; DEALLOCATE PREPARE s;

SET FOREIGN_KEY_CHECKS = @OLD_FK;

SELECT '✅ Rebuilt yarn_attribute_values (new AI PK, unique(yarn_id, attribute_id), FKs restored)' AS result, NOW() AS done_at;
