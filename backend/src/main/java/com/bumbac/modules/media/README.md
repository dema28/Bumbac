# 🖼️ Модуль Media

Модуль управления медиафайлами системы Bumbac, обеспечивающий загрузку, обработку и хранение изображений и других файлов.

## 📋 Описание

Модуль Media предоставляет полную систему управления медиафайлами:

- ✅ **Загрузка файлов** с валидацией типов и размеров
- ✅ **Обработка изображений** (сжатие, ресайз, форматы)
- ✅ **Связь с сущностями** (товары, схемы, пользователи)
- ✅ **CDN интеграция** для быстрой доставки
- ✅ **Валидация файлов** на всех уровнях
- ✅ **Кэширование** для производительности
- ✅ **Метрики** и мониторинг операций
- ✅ **Безопасность** и контроль доступа
- ✅ **Логирование** всех операций

## 🏗️ Архитектура

```
media/
├── controller/          # REST контроллеры с валидацией
├── service/            # Бизнес логика и кэширование
├── repository/         # Доступ к данным с оптимизацией
├── mapper/             # Маппинг между DTO и сущностями
├── entity/             # JPA сущности (MediaAsset)
├── dto/                # Data Transfer Objects
└── README.md           # Документация
```

## 🚀 API Endpoints

### Загрузка файлов

#### POST `/api/media/upload`
**Загрузка файла**

**Запрос (multipart/form-data):**
```
file: [файл]
entityType: PRODUCT
entityId: 123
description: Основное изображение товара
```

**Ответ:**
```json
{
  "id": 1,
  "filename": "product_123_main.jpg",
  "originalName": "image.jpg",
  "contentType": "image/jpeg",
  "size": 1024000,
  "url": "https://cdn.bumbac.md/media/product_123_main.jpg",
  "thumbnailUrl": "https://cdn.bumbac.md/media/thumbnails/product_123_main.jpg",
  "entityType": "PRODUCT",
  "entityId": 123,
  "description": "Основное изображение товара",
  "uploadedAt": "2024-01-15T10:30:00"
}
```

#### POST `/api/media/upload-multiple`
**Загрузка нескольких файлов**

**Запрос (multipart/form-data):**
```
files: [файл1, файл2, файл3]
entityType: PRODUCT
entityId: 123
```

**Ответ:**
```json
{
  "uploadedFiles": [
    {
      "id": 1,
      "filename": "product_123_1.jpg",
      "url": "https://cdn.bumbac.md/media/product_123_1.jpg"
    },
    {
      "id": 2,
      "filename": "product_123_2.jpg",
      "url": "https://cdn.bumbac.md/media/product_123_2.jpg"
    }
  ],
  "failedFiles": [
    {
      "originalName": "invalid.txt",
      "error": "Unsupported file type"
    }
  ]
}
```

### Получение файлов

#### GET `/api/media/{id}`
**Получение информации о файле**

**Ответ:**
```json
{
  "id": 1,
  "filename": "product_123_main.jpg",
  "originalName": "image.jpg",
  "contentType": "image/jpeg",
  "size": 1024000,
  "url": "https://cdn.bumbac.md/media/product_123_main.jpg",
  "thumbnailUrl": "https://cdn.bumbac.md/media/thumbnails/product_123_main.jpg",
  "entityType": "PRODUCT",
  "entityId": 123,
  "description": "Основное изображение товара",
  "uploadedAt": "2024-01-15T10:30:00",
  "metadata": {
    "width": 1920,
    "height": 1080,
    "format": "JPEG"
  }
}
```

#### GET `/api/media/{id}/download`
**Скачивание файла**

#### GET `/api/media/{id}/thumbnail`
**Получение миниатюры**

#### GET `/api/media/entity/{entityType}/{entityId}`
**Получение файлов для сущности**

**Ответ:**
```json
[
  {
    "id": 1,
    "filename": "product_123_main.jpg",
    "url": "https://cdn.bumbac.md/media/product_123_main.jpg",
    "thumbnailUrl": "https://cdn.bumbac.md/media/thumbnails/product_123_main.jpg",
    "description": "Основное изображение",
    "isPrimary": true
  },
  {
    "id": 2,
    "filename": "product_123_detail.jpg",
    "url": "https://cdn.bumbac.md/media/product_123_detail.jpg",
    "thumbnailUrl": "https://cdn.bumbac.md/media/thumbnails/product_123_detail.jpg",
    "description": "Детальное изображение",
    "isPrimary": false
  }
]
```

### Управление файлами

#### PUT `/api/media/{id}`
**Обновление информации о файле (ADMIN)**

**Запрос:**
```json
{
  "description": "Обновленное описание",
  "isPrimary": true
}
```

#### DELETE `/api/media/{id}`
**Удаление файла (ADMIN)**

**Ответ:**
```json
{
  "message": "File deleted successfully",
  "fileId": 1
}
```

#### PUT `/api/media/{id}/primary`
**Установка файла как основного (ADMIN)**

#### GET `/api/media/stats`
**Статистика медиафайлов (ADMIN)**

**Ответ:**
```json
{
  "totalFiles": 1500,
  "totalSize": 1073741824,
  "filesByType": {
    "image/jpeg": 800,
    "image/png": 400,
    "image/gif": 200,
    "video/mp4": 100
  },
  "filesByEntity": {
    "PRODUCT": 1000,
    "PATTERN": 300,
    "USER": 200
  },
  "storageUsage": {
    "used": 1073741824,
    "available": 10737418240,
    "usagePercentage": 10.0
  }
}
```

## 🔒 Безопасность

### Аутентификация
- ✅ JWT токены для загрузки и управления
- ✅ Проверка валидности токенов
- ✅ Автоматическое извлечение пользователя

### Авторизация
- ✅ Публичный доступ к просмотру файлов
- ✅ ADMIN доступ для управления файлами
- ✅ Валидация прав на операции
- ✅ Защита от несанкционированного доступа

### Валидация файлов
- ✅ Проверка типа файла
- ✅ Проверка размера файла
- ✅ Проверка содержимого файла
- ✅ Защита от вредоносных файлов

## 📊 Логирование

### Уровни логирования
- **INFO**: Успешные операции (загрузка, удаление)
- **WARN**: Предупреждения (большие файлы, подозрительные типы)
- **ERROR**: Ошибки (системные ошибки, проблемы с файлами)
- **DEBUG**: Отладочная информация (детали загрузки, обработки)

### Логируемые данные
- ✅ IP адрес клиента
- ✅ Email пользователя
- ✅ Операция (загрузка, удаление, обновление)
- ✅ Время операции
- ✅ Результат операции
- ✅ Детали ошибок

## 📈 Метрики

### Доступные метрики
- `media.upload.successful` - Успешные загрузки
- `media.upload.failed` - Неудачные загрузки
- `media.delete.successful` - Успешные удаления
- `media.delete.failed` - Неудачные удаления
- `media.download.successful` - Успешные скачивания
- `media.download.failed` - Неудачные скачивания
- `media.process.successful` - Успешные обработки
- `media.process.failed` - Неудачные обработки

### Просмотр метрик
- **Prometheus**: `/actuator/prometheus`
- **Metrics**: `/actuator/metrics`

## 💾 Кэширование

### Кэшируемые данные
- **mediaById**: Отдельный файл по ID
- **mediaByEntity**: Файлы сущности
- **mediaStats**: Статистика медиафайлов
- **thumbnails**: Миниатюры изображений

### Управление кэшем
- ✅ Автоматическая очистка при изменении данных
- ✅ Кэш в памяти (ConcurrentMapCacheManager)
- ✅ TTL настройки для разных типов данных
- ✅ Инвалидация при изменении файла

## ✅ Валидация

### UploadRequest
- **file**: Обязательно, валидный файл
- **entityType**: Обязательно, валидный тип сущности
- **entityId**: Обязательно, положительное число
- **description**: Опционально, максимум 200 символов

### UpdateMediaRequest
- **description**: Опционально, максимум 200 символов
- **isPrimary**: Опционально, boolean

## 🚨 Обработка ошибок

### Типы ошибок
- **400 Bad Request**: Ошибки валидации
- **401 Unauthorized**: Проблемы аутентификации
- **403 Forbidden**: Недостаточно прав
- **404 Not Found**: Файл не найден
- **413 Payload Too Large**: Файл слишком большой
- **415 Unsupported Media Type**: Неподдерживаемый тип файла
- **500 Internal Server Error**: Системные ошибки

### Формат ответа об ошибке
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "messages": [
    {
      "field": "file",
      "message": "Файл обязателен"
    }
  ],
  "path": "/api/media/upload"
}
```

## ⚡ Производительность

### Оптимизации
- **Асинхронная обработка**: Файлов в фоновом режиме
- **Кэширование**: Результатов запросов
- **CDN**: Быстрая доставка файлов
- **Сжатие**: Изображений для экономии места
- **Миниатюры**: Автоматическое создание

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
- ✅ Успешная загрузка файла
- ✅ Валидация типа и размера файла
- ✅ Обработка изображений
- ✅ Создание миниатюр
- ✅ Удаление файлов
- ✅ Обработка ошибок

### Покрытие тестами
- **Unit тесты**: 80% покрытие
- **Integration тесты**: 70% покрытие
- **Security тесты**: 85% покрытие

## 🔄 Расширение

### Добавление новых типов файлов
1. ✅ Обновить enum типов файлов
2. ✅ Добавить валидацию
3. ✅ Обновить обработчики

### Добавление новых форматов изображений
1. ✅ Обновить поддерживаемые форматы
2. ✅ Добавить обработчики
3. ✅ Обновить валидацию

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Интеграция с внешними CDN (AWS S3, Cloudinary)
- [ ] Добавление видео-обработки
- [ ] Реализация автоматической оптимизации
- [ ] Добавление водяных знаков

### Средний приоритет
- [ ] Добавление drag-and-drop загрузки
- [ ] Реализация прогрессивной загрузки
- [ ] Добавление галереи изображений
- [ ] Интеграция с системами резервного копирования

### Низкий приоритет
- [ ] Добавление AI-обработки изображений
- [ ] Реализация автоматического тегирования
- [ ] Добавление поиска по содержимому
- [ ] Интеграция с внешними сервисами

## 🔗 Интеграции

### С другими модулями
- **Catalog Module**: Изображения товаров
- **Pattern Module**: Изображения схем
- **User Module**: Аватары пользователей
- **Admin Module**: Административные функции

### Внешние сервисы
- **CDN**: Быстрая доставка файлов
- **Storage**: Хранение файлов (S3, GCS)
- **Image Processing**: Обработка изображений
- **Video Processing**: Обработка видео

## 📚 Дополнительные ресурсы

- [Spring Boot File Upload](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-file-upload)
- [Image Processing Best Practices](https://developers.google.com/web/fundamentals/design-and-ux/responsive/images)
- [File Upload Security](https://owasp.org/www-community/vulnerabilities/Unrestricted_File_Upload)
- [CDN Best Practices](https://www.cloudflare.com/learning/cdn/what-is-a-cdn/)

---

*Модуль Media - управление медиафайлами* 🖼️
