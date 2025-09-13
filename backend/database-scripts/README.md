# Database Scripts for Bumbac LOCAL Development

Набор утилит для валидации, диагностики и мониторинга базы данных `yarn_store_local` в процессе разработки.

## Структура файлов

```
database-scripts/
├── README.md                        # Этот файл
├── db_code_validation_script.sql    # Универсальная проверка БД ↔ Java Entity
└── local/                           # LOCAL окружение специфичные скрипты
    ├── local_db_diagnostic.sql      # Быстрая диагностика LOCAL среды  
    ├── continuous_monitoring.sql    # Ежедневный мониторинг с оценкой
    └── automatic_fixes_generator.sql # Генератор DDL команд исправления
```

---

## LOCAL окружение

- **База данных**: `yarn_store_local`
- **Подключение**: `localhost:3307` (Docker MySQL)
- **Пользователь**: `denis` / `local123`
- **Назначение**: Разработка и отладка

---

## Быстрый старт

### Подключение к LOCAL базе
```bash
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local
```

### Проверка после миграций
```bash
cd ~/Bumbac/backend/database-scripts

# Универсальная проверка Entity соответствия (2 мин)
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql

# Быстрая LOCAL диагностика (30 сек)
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/local_db_diagnostic.sql
```

---

## Подробное использование

### db_code_validation_script.sql (Универсальный)
**Назначение**: Проверка соответствия структуры БД и Java Entity классов

**Когда использовать**:
- После применения новых миграций
- При изменении Entity классов
- Перед коммитом изменений
- При подготовке к деплою

```bash
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql > validation_report.txt
less validation_report.txt
```

**Что проверяет**:
- Соответствие имен полей (@Column annotations)
- Типы данных и ограничения (@NotNull, @Size)
- Foreign Key связи (@JoinColumn, @OneToMany)
- Индексы из @Index аннотаций
- Enum значения vs database статусы
- CASCADE правила vs orphanRemoval настройки

### local/local_db_diagnostic.sql
**Назначение**: Быстрая проверка здоровья LOCAL среды

**Когда использовать**:
- Каждое утро перед работой
- После применения исправлений V1.1
- При подозрении на проблемы с БД

```bash
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/local_db_diagnostic.sql
```

**Что показывает**:
- Статус критических исправлений (V1.1__schema_fixes.sql)
- Статистика по таблицам
- Entity compliance score
- Foreign Key нарушения
- Критические предупреждения

### local/continuous_monitoring.sql
**Назначение**: Подробный мониторинг с системой оценок

**Когда использовать**:
- Еженедельная проверка
- Подготовка отчетов о состоянии БД
- Анализ качества данных

```bash
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/continuous_monitoring.sql > weekly_report.txt
```

**Что включает**:
- Health Score система (0-100)
- Анализ качества данных
- Производительность индексов
- Активность пользователей
- Рекомендации по улучшению

### local/automatic_fixes_generator.sql
**Назначение**: Генерация DDL команд для исправления проблем

**Когда использовать**:
- При обнаружении проблем в валидации
- Для подготовки migration исправлений
- Автоматизация рутинных исправлений

```bash
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/automatic_fixes_generator.sql > fixes_to_apply.sql

# Просмотр сгенерированных команд
less fixes_to_apply.sql

# ОСТОРОЖНО: Применение только после проверки!
# mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < fixes_to_apply.sql
```

---

## Типичные сценарии LOCAL разработки

### После применения V1.1__schema_fixes.sql
```bash
# Проверяем что критические исправления применились
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/local_db_diagnostic.sql | grep "FIXED"
```

### Подготовка к коммиту
```bash
# Полная проверка перед git commit
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql > pre_commit_check.txt

# Ищем критические проблемы
grep -E "(❌|🔴|CRITICAL)" pre_commit_check.txt
```

### Еженедельный мониторинг LOCAL
```bash
# Создаем отчет с датой
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/continuous_monitoring.sql > "local_weekly_report_$(date +%Y%m%d).txt"
```

### Автоматическое исправление проблем
```bash
# Генерируем исправления для LOCAL
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/automatic_fixes_generator.sql > local_auto_fixes.sql

# Применяем с осторожностью
cat local_auto_fixes.sql  # ОБЯЗАТЕЛЬНО просмотрите перед применением!
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local_auto_fixes.sql
```

---

## Интерпретация результатов

### Health Score
- **90-100**: 🟢 Отлично - готово к переносу в staging
- **75-89**: 🟡 Хорошо - мелкие оптимизации
- **60-74**: 🟠 Требуется внимание
- **< 60**: 🔴 Критические проблемы

### Entity Compliance
- **95-100%**: Полное соответствие Entity классам
- **80-94%**: Незначительные расхождения
- **60-79%**: Существенные проблемы
- **< 60%**: Критические несоответствия

### Статусы проверок
- ✅ **FIXED** - исправление V1.1 применено успешно
- ⚠️ **NEEDS ATTENTION** - требует ручного вмешательства
- ❌ **MISSING** - критическое поле/таблица отсутствует
- 🔴 **CRITICAL** - блокирующая проблема

---

## Автоматизация LOCAL разработки

### Ежедневная проверка
```bash
# Добавьте в ваш daily workflow
alias db-check="mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < ~/Bumbac/backend/database-scripts/local/local_db_diagnostic.sql"
```

### Pre-commit hook
```bash
# Создайте .git/hooks/pre-commit
#!/bin/bash
cd backend/database-scripts
ISSUES=$(mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql | grep -c "❌\|🔴\|CRITICAL")
if [ $ISSUES -gt 0 ]; then
    echo "❌ Database validation failed. Fix issues before commit."
    exit 1
fi
echo "✅ Database validation passed"
```

### VS Code/IntelliJ интеграция
Создайте задачу для быстрой проверки:
- **Команда**: `mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local`
- **Аргументы**: `< database-scripts/local/local_db_diagnostic.sql`

---

## Решение типичных LOCAL проблем

### yarn_attribute_values.attribute_id missing
```bash
# Примените исправления V1.1
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < ../src/main/resources/db/migration/V1.1__schema_fixes.sql
```

### discount_rules enum type wrong
```sql
ALTER TABLE discount_rules CHANGE COLUMN type type enum('PERCENTAGE','FIXED_AMOUNT','FREE_SHIPPING') NOT NULL;
```

### Missing indexes на foreign keys
```sql
CREATE INDEX idx_colors_yarn_id ON colors(yarn_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
```

### Orders missing status_id FK
```sql
ALTER TABLE orders ADD COLUMN status_id bigint NULL;
UPDATE orders SET status_id = 1 WHERE status = 'NEW';
ALTER TABLE orders MODIFY COLUMN status_id bigint NOT NULL;
```

---

## Workflow LOCAL → STAGING

Когда LOCAL среда стабильна и готова к переносу:

### 1. Финальная проверка LOCAL
```bash
# Убедитесь что все зеленое
mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql > final_local_check.txt
grep -E "(❌|🔴|CRITICAL)" final_local_check.txt || echo "✅ LOCAL готов к staging"
```

### 2. Подготовка миграций для staging
```bash
# Скопируйте финальные миграции V1-V9
cp ../src/main/resources/db/migration/V*.sql /staging-migration-package/

# Создайте адаптированные скрипты для staging
mkdir -p ../staging
sed 's/yarn_store_local/yarn_store_staging/g' local/*.sql > ../staging/
```

### 3. Документирование изменений
```bash
# Создайте отчет о готовности
echo "LOCAL Environment Ready for STAGING Migration
Date: $(date)
Health Score: $(mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < local/continuous_monitoring.sql | grep 'overall_health_score')
Critical Issues: $(mysql -u denis -p'local123' -h localhost -P 3307 yarn_store_local < db_code_validation_script.sql | grep -c '❌\|🔴')
" > local_to_staging_ready.txt
```

---

## Поддержка и помощь

При возникновении проблем в LOCAL:

1. **Быстрая диагностика**: `local/local_db_diagnostic.sql`
2. **Автоматические исправления**: `local/automatic_fixes_generator.sql`
3. **Полная валидация**: `db_code_validation_script.sql`
4. **Проверьте логи**: `tail -f ~/Bumbac/backend/logs/spring.log`

**Важно**: Всегда делайте backup LOCAL БД перед применением автоматических исправлений!

```bash
# Backup LOCAL БД
mysqldump -u denis -p'local123' -h localhost -P 3307 yarn_store_local > "backup_local_$(date +%Y%m%d_%H%M%S).sql"
```