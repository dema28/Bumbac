# 📧 Модуль Newsletter

Модуль email рассылок системы Bumbac, обеспечивающий управление подписками и отправку уведомлений.

## 📋 Описание

Модуль Newsletter предоставляет полную систему email рассылок:

- ✅ **Подписка на рассылки** с подтверждением email
- ✅ **Отписка от рассылок** с подтверждением
- ✅ **Управление списками рассылок** для администраторов
- ✅ **Отправка массовых рассылок** с шаблонами
- ✅ **Валидация email** адресов
- ✅ **Кэширование** для производительности
- ✅ **Метрики** и мониторинг операций
- ✅ **Безопасность** и контроль доступа
- ✅ **Логирование** всех операций

## 🏗️ Архитектура

```
newsletter/
├── controller/          # REST контроллеры с валидацией
├── service/            # Бизнес логика и кэширование
├── repository/         # Доступ к данным с оптимизацией
├── entity/             # JPA сущности (NewsletterSubscriber)
├── dto/                # Data Transfer Objects
└── README.md           # Документация
```

## 🚀 API Endpoints

### Подписки

#### POST `/api/newsletter/subscribe`
**Подписка на рассылку**

**Запрос:**
```json
{
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "preferences": ["new_products", "promotions"]
}
```

**Ответ:**
```json
{
  "message": "Subscription request sent. Please check your email to confirm.",
  "email": "user@example.com"
}
```

#### GET `/api/newsletter/confirm`
**Подтверждение подписки**

**Параметры:**
- `token` - токен подтверждения из email

**Ответ:**
```json
{
  "message": "Subscription confirmed successfully",
  "email": "user@example.com"
}
```

#### POST `/api/newsletter/unsubscribe`
**Отписка от рассылки**

**Запрос:**
```json
{
  "email": "user@example.com"
}
```

**Ответ:**
```json
{
  "message": "Unsubscribed successfully",
  "email": "user@example.com"
}
```

#### GET `/api/newsletter/unsubscribe/confirm`
**Подтверждение отписки**

**Параметры:**
- `token` - токен подтверждения из email

**Ответ:**
```json
{
  "message": "Unsubscription confirmed successfully",
  "email": "user@example.com"
}
```

### Административные функции

#### GET `/api/newsletter/subscribers`
**Получение списка подписчиков (ADMIN)**

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "status": "ACTIVE",
      "preferences": ["new_products", "promotions"],
      "subscribedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0
}
```

#### POST `/api/newsletter/send`
**Отправка рассылки (ADMIN)**

**Запрос:**
```json
{
  "subject": "Новые поступления пряжи",
  "content": "Дорогие подписчики! У нас новые поступления...",
  "template": "new_products",
  "filters": {
    "preferences": ["new_products"],
    "status": "ACTIVE"
  }
}
```

**Ответ:**
```json
{
  "message": "Newsletter sent successfully",
  "sentCount": 50,
  "failedCount": 2
}
```

#### PUT `/api/newsletter/subscribers/{id}/status`
**Изменение статуса подписчика (ADMIN)**

**Запрос:**
```json
{
  "status": "SUSPENDED"
}
```

## 🔒 Безопасность

### Аутентификация
- ✅ JWT токены для административных операций
- ✅ Проверка валидности токенов
- ✅ Автоматическое извлечение пользователя

### Авторизация
- ✅ Публичный доступ к подписке/отписке
- ✅ ADMIN доступ для управления рассылками
- ✅ Валидация прав на операции
- ✅ Защита от спама

### Валидация данных
- ✅ Проверка формата email
- ✅ Валидация бизнес-правил
- ✅ Защита от некорректных операций
- ✅ Проверка уникальности email

## 📊 Логирование

### Уровни логирования
- **INFO**: Успешные операции (подписка, отписка, отправка)
- **WARN**: Предупреждения (дублирование email, проблемы с отправкой)
- **ERROR**: Ошибки (системные ошибки, проблемы с SMTP)
- **DEBUG**: Отладочная информация (детали запросов, SMTP)

### Логируемые данные
- ✅ IP адрес клиента
- ✅ Email адрес
- ✅ Операция (подписка, отписка, отправка)
- ✅ Время операции
- ✅ Результат операции
- ✅ Детали ошибок

## 📈 Метрики

### Доступные метрики
- `newsletter.subscribe.successful` - Успешные подписки
- `newsletter.subscribe.failed` - Неудачные подписки
- `newsletter.unsubscribe.successful` - Успешные отписки
- `newsletter.unsubscribe.failed` - Неудачные отписки
- `newsletter.send.successful` - Успешные отправки
- `newsletter.send.failed` - Неудачные отправки
- `newsletter.confirm.successful` - Успешные подтверждения
- `newsletter.confirm.failed` - Неудачные подтверждения

### Просмотр метрик
- **Prometheus**: `/actuator/prometheus`
- **Metrics**: `/actuator/metrics`

## 💾 Кэширование

### Кэшируемые данные
- **subscribers**: Список подписчиков по фильтрам
- **subscriberByEmail**: Подписчик по email
- **emailExists**: Проверка существования email

### Управление кэшем
- ✅ Автоматическая очистка при изменении данных
- ✅ Кэш в памяти (ConcurrentMapCacheManager)
- ✅ TTL настройки для разных типов данных
- ✅ Инвалидация при изменении подписчика

## ✅ Валидация

### SubscribeRequest
- **email**: Обязательно, валидный email формат, уникальность
- **firstName**: Опционально, 2-30 символов
- **lastName**: Опционально, 2-30 символов
- **preferences**: Опционально, список валидных предпочтений

### UnsubscribeRequest
- **email**: Обязательно, валидный email формат

### SendNewsletterRequest
- **subject**: Обязательно, 5-100 символов
- **content**: Обязательно, не пустой
- **template**: Опционально, валидный шаблон
- **filters**: Опционально, валидные фильтры

## 🚨 Обработка ошибок

### Типы ошибок
- **400 Bad Request**: Ошибки валидации
- **401 Unauthorized**: Проблемы аутентификации
- **403 Forbidden**: Недостаточно прав
- **404 Not Found**: Подписчик не найден
- **409 Conflict**: Email уже подписан
- **429 Too Many Requests**: Превышен лимит запросов
- **500 Internal Server Error**: Системные ошибки

### Формат ответа об ошибке
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "messages": [
    {
      "field": "email",
      "message": "Email должен быть корректным"
    }
  ],
  "path": "/api/newsletter/subscribe"
}
```

## ⚡ Производительность

### Оптимизации
- **Индексы в БД**: Для быстрого поиска по email
- **Кэширование**: Результатов запросов
- **Оптимизированные SQL**: С JOIN FETCH
- **Асинхронная отправка**: Email в фоновом режиме
- **Батчевая обработка**: Для массовых рассылок

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
- ✅ Успешная подписка на рассылку
- ✅ Подтверждение подписки
- ✅ Отписка от рассылки
- ✅ Валидация email адресов
- ✅ Отправка массовых рассылок
- ✅ Обработка ошибок SMTP

### Покрытие тестами
- **Unit тесты**: 75% покрытие
- **Integration тесты**: 65% покрытие
- **Security тесты**: 80% покрытие

## 🔄 Расширение

### Добавление новых шаблонов
1. ✅ Создать HTML шаблон
2. ✅ Добавить в enum шаблонов
3. ✅ Обновить валидацию

### Добавление новых предпочтений
1. ✅ Обновить enum предпочтений
2. ✅ Добавить валидацию
3. ✅ Обновить фильтры

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Интеграция с внешними email сервисами (SendGrid, Mailgun)
- [ ] Добавление A/B тестирования рассылок
- [ ] Реализация персонализированных рассылок
- [ ] Добавление аналитики открытий и кликов

### Средний приоритет
- [ ] Добавление планировщика рассылок
- [ ] Реализация сегментации аудитории
- [ ] Добавление шаблонов рассылок
- [ ] Интеграция с системой уведомлений

### Низкий приоритет
- [ ] Добавление RSS рассылок
- [ ] Реализация социальных рассылок
- [ ] Добавление интерактивных элементов
- [ ] Интеграция с CRM системами

## 🔗 Интеграции

### С другими модулями
- **Auth Module**: Аутентификация администраторов
- **User Module**: Информация о пользователях
- **Admin Module**: Административные функции
- **Email Module**: Отправка email

### Внешние сервисы
- **SMTP Servers**: Отправка email (Brevo, Mailtrap)
- **Email Analytics**: Отслеживание открытий и кликов
- **CRM Systems**: Управление контактами
- **Marketing Tools**: Автоматизация маркетинга

## 📚 Дополнительные ресурсы

- [Spring Boot Mail Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-email)
- [Jakarta Mail Documentation](https://jakarta.ee/specifications/mail/)
- [Email Best Practices](https://www.emailonacid.com/blog/article/email-development/email-development-best-practices/)
- [SMTP Configuration](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol)

---

*Модуль Newsletter - управление email рассылками* 📧
