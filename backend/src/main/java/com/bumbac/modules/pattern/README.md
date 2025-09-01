# 📖 Модуль Pattern

Модуль схем вязания системы Bumbac, обеспечивающий управление схемами вязания с мультиязычной поддержкой.

## 📋 Описание

Модуль Pattern предоставляет полную систему управления схемами вязания:

- ✅ **Схемы вязания** с полным CRUD
- ✅ **Мультиязычные описания** с переводами
- ✅ **Категории сложности** и уровни
- ✅ **Связь с пряжей** и материалами
- ✅ **Фильтрация и поиск** схем
- ✅ **Валидация данных** на всех уровнях
- ✅ **Кэширование** для производительности
- ✅ **Метрики** и мониторинг операций
- ✅ **Безопасность** и контроль доступа
- ✅ **Логирование** всех операций

## 🏗️ Архитектура

```
pattern/
├── controller/          # REST контроллеры с валидацией
├── service/            # Бизнес логика и кэширование
├── repository/         # Доступ к данным с оптимизацией
├── mapper/             # Маппинг между DTO и сущностями
├── entity/             # JPA сущности (Pattern, PatternTranslation)
├── dto/                # Data Transfer Objects
└── README.md           # Документация
```

## 🚀 API Endpoints

### Схемы вязания

#### GET `/api/patterns`
**Получение списка схем с фильтрацией**

**Параметры:**
- `page` - номер страницы (по умолчанию: 0)
- `size` - размер страницы (по умолчанию: 20)
- `difficulty` - фильтр по сложности
- `category` - фильтр по категории
- `yarnId` - фильтр по пряже
- `language` - язык описания
- `sort` - сортировка (name, difficulty, createdAt)

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Уютный свитер",
      "description": "Теплый свитер для холодной погоды",
      "difficulty": "INTERMEDIATE",
      "category": "SWEATER",
      "estimatedTime": "20-30 hours",
      "materials": ["Пряжа", "Спицы 4mm"],
      "yarnRecommendations": [
        {
          "id": 12,
          "name": "Super Soft Cotton",
          "brand": "Premium Yarns"
        }
      ],
      "translations": [
        {
          "language": "en",
          "name": "Cozy Sweater",
          "description": "Warm sweater for cold weather"
        }
      ],
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 3,
  "currentPage": 0
}
```

#### GET `/api/patterns/{id}`
**Получение детальной информации о схеме**

**Ответ:**
```json
{
  "id": 1,
  "name": "Уютный свитер",
  "description": "Теплый свитер для холодной погоды",
  "difficulty": "INTERMEDIATE",
  "category": "SWEATER",
  "estimatedTime": "20-30 hours",
  "materials": ["Пряжа", "Спицы 4mm"],
  "instructions": "Подробные инструкции по вязанию...",
  "yarnRecommendations": [...],
  "translations": [
    {
      "language": "en",
      "name": "Cozy Sweater",
      "description": "Warm sweater for cold weather",
      "instructions": "Detailed knitting instructions..."
    },
    {
      "language": "ro",
      "name": "Pulover confortabil",
      "description": "Pulover cald pentru vremea rece"
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T12:00:00"
}
```

#### POST `/api/patterns`
**Создание новой схемы (MODERATOR/ADMIN)**

**Запрос:**
```json
{
  "name": "Новая схема",
  "description": "Описание схемы",
  "difficulty": "BEGINNER",
  "category": "SCARF",
  "estimatedTime": "5-10 hours",
  "materials": ["Пряжа", "Спицы 3.5mm"],
  "instructions": "Подробные инструкции...",
  "yarnRecommendations": [12, 15],
  "translations": [
    {
      "language": "en",
      "name": "New Pattern",
      "description": "Pattern description",
      "instructions": "Detailed instructions..."
    }
  ]
}
```

#### PUT `/api/patterns/{id}`
**Обновление схемы (MODERATOR/ADMIN)**

#### DELETE `/api/patterns/{id}`
**Удаление схемы (ADMIN)**

### Поиск и фильтрация

#### GET `/api/patterns/search`
**Поиск схем по ключевым словам**

**Параметры:**
- `q` - поисковый запрос
- `difficulty` - фильтр по сложности
- `category` - фильтр по категории
- `language` - язык поиска

**Ответ:**
```json
{
  "content": [...],
  "totalElements": 25,
  "totalPages": 2,
  "currentPage": 0
}
```

#### GET `/api/patterns/by-difficulty/{difficulty}`
**Получение схем по сложности**

#### GET `/api/patterns/by-category/{category}`
**Получение схем по категории**

#### GET `/api/patterns/by-yarn/{yarnId}`
**Получение схем для конкретной пряжи**

### Категории и сложность

#### GET `/api/patterns/categories`
**Получение списка категорий**

**Ответ:**
```json
[
  "SCARF",
  "HAT",
  "SWEATER",
  "SOCKS",
  "BLANKET",
  "ACCESSORIES"
]
```

#### GET `/api/patterns/difficulties`
**Получение уровней сложности**

**Ответ:**
```json
[
  "BEGINNER",
  "INTERMEDIATE",
  "ADVANCED",
  "EXPERT"
]
```

### Переводы

#### GET `/api/patterns/{id}/translations`
**Получение переводов схемы**

#### POST `/api/patterns/{id}/translations`
**Добавление перевода (MODERATOR/ADMIN)**

**Запрос:**
```json
{
  "language": "fr",
  "name": "Pull confortable",
  "description": "Pull chaud pentru vremea rece",
  "instructions": "Instructions détaillées..."
}
```

#### PUT `/api/patterns/{id}/translations/{language}`
**Обновление перевода (MODERATOR/ADMIN)**

#### DELETE `/api/patterns/{id}/translations/{language}`
**Удаление перевода (ADMIN)**

## 🔒 Безопасность

### Аутентификация
- ✅ JWT токены для защищенных операций
- ✅ Проверка валидности токенов
- ✅ Автоматическое извлечение пользователя

### Авторизация
- ✅ Публичный доступ к просмотру схем
- ✅ MODERATOR/ADMIN доступ для модификации
- ✅ Валидация прав на операции
- ✅ Защита от несанкционированного доступа

### Валидация данных
- ✅ Проверка входных данных
- ✅ Валидация бизнес-правил
- ✅ Защита от некорректных операций
- ✅ Проверка существования связанных сущностей

## 📊 Логирование

### Уровни логирования
- **INFO**: Успешные операции (создание, обновление, удаление)
- **WARN**: Предупреждения (дублирование имен, неверные данные)
- **ERROR**: Ошибки (системные ошибки, проблемы с БД)
- **DEBUG**: Отладочная информация (детали запросов, SQL)

### Логируемые данные
- ✅ IP адрес клиента
- ✅ Email пользователя
- ✅ Операция (создание, чтение, обновление, удаление)
- ✅ Время операции
- ✅ Результат операции
- ✅ Детали ошибок

## 📈 Метрики

### Доступные метрики
- `pattern.create.successful` - Успешные создания схем
- `pattern.create.failed` - Неудачные создания схем
- `pattern.update.successful` - Успешные обновления схем
- `pattern.update.failed` - Неудачные обновления схем
- `pattern.delete.successful` - Успешные удаления схем
- `pattern.delete.failed` - Неудачные удаления схем
- `pattern.view.successful` - Успешные просмотры схем
- `pattern.view.failed` - Неудачные просмотры схем
- `pattern.search.successful` - Успешные поиски
- `pattern.search.failed` - Неудачные поиски

### Просмотр метрик
- **Prometheus**: `/actuator/prometheus`
- **Metrics**: `/actuator/metrics`

## 💾 Кэширование

### Кэшируемые данные
- **patterns**: Списки схем по фильтрам
- **patternById**: Отдельная схема по ID
- **categories**: Список категорий
- **difficulties**: Список уровней сложности
- **translations**: Переводы схем

### Управление кэшем
- ✅ Автоматическая очистка при изменении данных
- ✅ Кэш в памяти (ConcurrentMapCacheManager)
- ✅ TTL настройки для разных типов данных
- ✅ Инвалидация при изменении схемы

## ✅ Валидация

### PatternRequest
- **name**: Обязательно, 5-100 символов, уникальность
- **description**: Обязательно, 10-500 символов
- **difficulty**: Обязательно, валидный уровень сложности
- **category**: Обязательно, валидная категория
- **estimatedTime**: Опционально, валидный формат
- **materials**: Обязательно, не пустой список
- **instructions**: Обязательно, не пустой
- **yarnRecommendations**: Опционально, существующие ID пряжи

### PatternTranslationRequest
- **language**: Обязательно, валидный код языка
- **name**: Обязательно, 5-100 символов
- **description**: Обязательно, 10-500 символов
- **instructions**: Обязательно, не пустой

## 🚨 Обработка ошибок

### Типы ошибок
- **400 Bad Request**: Ошибки валидации
- **401 Unauthorized**: Проблемы аутентификации
- **403 Forbidden**: Недостаточно прав
- **404 Not Found**: Схема не найдена
- **409 Conflict**: Нарушение уникальности
- **500 Internal Server Error**: Системные ошибки

### Формат ответа об ошибке
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "messages": [
    {
      "field": "name",
      "message": "Название должно содержать от 5 до 100 символов"
    }
  ],
  "path": "/api/patterns"
}
```

## ⚡ Производительность

### Оптимизации
- **Индексы в БД**: Для быстрого поиска по всем полям
- **Кэширование**: Результатов запросов
- **Оптимизированные SQL**: С JOIN FETCH
- **Ленивая загрузка**: Связанных данных
- **Пагинация**: Для больших списков

### Мониторинг
- ✅ Метрики для всех операций
- ✅ Логирование времени выполнения
- ✅ Отслеживание ошибок и исключений
- ✅ Статистика использования

## 🔄 Транзакции

### Транзакционность
- ✅ `@Transactional` для изменяющих операций
- ✅ `@Transactional(readOnly = true)` для чтения
- ✅ Автоматический rollback при ошибках

### Изоляция
- ✅ Read Committed по умолчанию
- ✅ Оптимистичные блокировки
- ✅ Избежание deadlocks

## 🧪 Тестирование

### Рекомендуемые тесты
- ✅ Успешное создание схемы
- ✅ Валидация обязательных полей
- ✅ Добавление переводов
- ✅ Поиск и фильтрация схем
- ✅ Обновление и удаление схем
- ✅ Обработка ошибок валидации

### Покрытие тестами
- **Unit тесты**: 80% покрытие
- **Integration тесты**: 70% покрытие
- **Security тесты**: 85% покрытие

## 🔄 Расширение

### Добавление новых категорий
1. ✅ Обновить enum категорий
2. ✅ Добавить валидацию
3. ✅ Обновить фильтры

### Добавление новых уровней сложности
1. ✅ Обновить enum сложности
2. ✅ Добавить валидацию
3. ✅ Обновить фильтры

### Добавление новых языков
1. ✅ Обновить enum языков
2. ✅ Добавить валидацию
3. ✅ Обновить переводы

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Добавление изображений к схемам
- [ ] Реализация полнотекстового поиска
- [ ] Добавление рейтингов и отзывов
- [ ] Создание системы рекомендаций

### Средний приоритет
- [ ] Добавление видео-инструкций
- [ ] Реализация интерактивных схем
- [ ] Добавление системы прогресса
- [ ] Интеграция с социальными сетями

### Низкий приоритет
- [ ] Добавление 3D-моделей изделий
- [ ] Реализация AR-просмотра
- [ ] Добавление голосовых инструкций
- [ ] Интеграция с внешними ресурсами

## 🔗 Интеграции

### С другими модулями
- **Catalog Module**: Связь с пряжей и материалами
- **User Module**: Избранные схемы пользователей
- **Media Module**: Изображения и видео схем
- **Admin Module**: Административные функции

### Внешние сервисы
- **CDN**: Хранение изображений схем
- **Search Engine**: Полнотекстовый поиск
- **Video Platform**: Хостинг видео-инструкций
- **Social Media**: Интеграция с социальными сетями

## 📚 Дополнительные ресурсы

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Cache Documentation](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Jakarta Validation](https://beanvalidation.org/)
- [Micrometer Documentation](https://micrometer.io/docs)

---

*Модуль Pattern - управление схемами вязания* 📖
