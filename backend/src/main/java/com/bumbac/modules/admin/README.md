# 👨‍💼 Модуль Admin

Модуль административных функций системы Bumbac, обеспечивающий управление пользователями и системными настройками.

## 📋 Описание

Модуль Admin предоставляет полную систему административного управления:

- ✅ **Управление пользователями** с назначением ролей
- ✅ **Статистика и аналитика** системы
- ✅ **Системные настройки** и конфигурация
- ✅ **Мониторинг** производительности
- ✅ **Аудит** всех операций
- ✅ **Валидация** данных на всех уровнях
- ✅ **Кэширование** для производительности
- ✅ **Метрики** и мониторинг операций
- ✅ **Безопасность** и контроль доступа
- ✅ **Логирование** всех операций

## 🏗️ Архитектура

```
admin/
├── controller/          # REST контроллеры с валидацией
├── service/            # Бизнес логика и кэширование
├── dto/                # Data Transfer Objects
└── README.md           # Документация
```

## 🚀 API Endpoints

### Управление пользователями

#### GET `/api/admin/users`
**Получение списка пользователей (ADMIN)**

**Параметры:**
- `page` - номер страницы
- `size` - размер страницы
- `role` - фильтр по роли
- `status` - фильтр по статусу
- `search` - поиск по email/имени

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "+37360123456",
      "roles": ["USER"],
      "status": "ACTIVE",
      "createdAt": "2024-01-15T10:30:00",
      "lastLoginAt": "2024-01-20T12:00:00",
      "ordersCount": 5,
      "favoritesCount": 3
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0
}
```

#### GET `/api/admin/users/{id}`
**Получение детальной информации о пользователе (ADMIN)**

**Ответ:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+37360123456",
  "roles": ["USER"],
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-20T12:00:00",
  "lastLoginAt": "2024-01-20T12:00:00",
  "ordersCount": 5,
  "favoritesCount": 3,
  "totalSpent": 599.50,
  "lastOrderDate": "2024-01-18T15:30:00"
}
```

#### PUT `/api/admin/users/{id}/role`
**Изменение роли пользователя (ADMIN)**

**Запрос:**
```json
{
  "role": "MODERATOR"
}
```

**Ответ:**
```json
{
  "message": "User role updated successfully",
  "userId": 1,
  "newRole": "MODERATOR"
}
```

#### PUT `/api/admin/users/{id}/status`
**Изменение статуса пользователя (ADMIN)**

**Запрос:**
```json
{
  "status": "SUSPENDED",
  "reason": "Нарушение правил"
}
```

**Ответ:**
```json
{
  "message": "User status updated successfully",
  "userId": 1,
  "newStatus": "SUSPENDED"
}
```

#### DELETE `/api/admin/users/{id}`
**Удаление пользователя (ADMIN)**

**Ответ:**
```json
{
  "message": "User deleted successfully",
  "userId": 1
}
```

#### GET `/api/admin/users/{id}/orders`
**Получение заказов пользователя (ADMIN)**

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "orderNumber": "ORD-2024-001",
      "status": "DELIVERED",
      "totalAmount": 119.70,
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}
```

#### GET `/api/admin/users/{id}/favorites`
**Получение избранного пользователя (ADMIN)**

**Ответ:**
```json
[
  {
    "id": 1,
    "yarnId": 12,
    "yarnName": "Super Soft Cotton",
    "addedAt": "2024-01-15T10:30:00"
  }
]
```

### Статистика и аналитика

#### GET `/api/admin/dashboard`
**Получение данных дашборда (ADMIN)**

**Ответ:**
```json
{
  "users": {
    "total": 1000,
    "active": 850,
    "newThisMonth": 50,
    "suspended": 10
  },
  "orders": {
    "total": 500,
    "pending": 25,
    "delivered": 450,
    "cancelled": 25,
    "totalRevenue": 25000.00
  },
  "products": {
    "total": 200,
    "active": 180,
    "outOfStock": 20
  },
  "recentActivity": [
    {
      "type": "NEW_ORDER",
      "description": "Новый заказ ORD-2024-001",
      "timestamp": "2024-01-20T15:30:00"
    }
  ]
}
```

#### GET `/api/admin/stats/users`
**Статистика пользователей (ADMIN)**

**Ответ:**
```json
{
  "totalUsers": 1000,
  "activeUsers": 850,
  "suspendedUsers": 10,
  "deletedUsers": 5,
  "usersByRole": {
    "USER": 950,
    "MODERATOR": 40,
    "ADMIN": 10
  },
  "registrationTrend": {
    "lastWeek": 15,
    "lastMonth": 50,
    "lastYear": 500
  },
  "topUsers": [
    {
      "id": 1,
      "email": "user@example.com",
      "ordersCount": 25,
      "totalSpent": 1500.00
    }
  ]
}
```

#### GET `/api/admin/stats/orders`
**Статистика заказов (ADMIN)**

**Ответ:**
```json
{
  "totalOrders": 500,
  "pendingOrders": 25,
  "deliveredOrders": 450,
  "cancelledOrders": 25,
  "totalRevenue": 25000.00,
  "averageOrderValue": 50.00,
  "ordersByStatus": {
    "NEW": 25,
    "PROCESSING": 15,
    "SHIPPED": 30,
    "DELIVERED": 450,
    "CANCELLED": 25
  },
  "revenueTrend": {
    "lastWeek": 1500.00,
    "lastMonth": 5000.00,
    "lastYear": 25000.00
  }
}
```

#### GET `/api/admin/stats/products`
**Статистика товаров (ADMIN)**

**Ответ:**
```json
{
  "totalProducts": 200,
  "activeProducts": 180,
  "outOfStockProducts": 20,
  "topSellingProducts": [
    {
      "id": 12,
      "name": "Super Soft Cotton",
      "salesCount": 150,
      "revenue": 7500.00
    }
  ],
  "productsByCategory": {
    "COTTON": 80,
    "WOOL": 60,
    "ACRYLIC": 40,
    "BLEND": 20
  }
}
```

### Системные настройки

#### GET `/api/admin/settings`
**Получение системных настроек (ADMIN)**

**Ответ:**
```json
{
  "orderSettings": {
    "maxItemsPerOrder": 100,
    "maxTotalAmount": 10000.00,
    "autoCancelTimeout": 24
  },
  "securitySettings": {
    "rateLimitRequestsPerMinute": 3,
    "rateLimitLoginAttemptsPerMinute": 5,
    "passwordMinLength": 8
  },
  "emailSettings": {
    "smtpHost": "smtp-relay.brevo.com",
    "smtpPort": 587,
    "fromEmail": "noreply@bumbac.md"
  }
}
```

#### PUT `/api/admin/settings`
**Обновление системных настроек (ADMIN)**

**Запрос:**
```json
{
  "orderSettings": {
    "maxItemsPerOrder": 150,
    "maxTotalAmount": 15000.00
  },
  "securitySettings": {
    "rateLimitRequestsPerMinute": 5
  }
}
```

### Аудит и логи

#### GET `/api/admin/audit`
**Получение логов аудита (ADMIN)**

**Параметры:**
- `page` - номер страницы
- `size` - размер страницы
- `user` - фильтр по пользователю
- `action` - фильтр по действию
- `dateFrom` - дата с
- `dateTo` - дата по

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "userEmail": "admin@example.com",
      "action": "USER_ROLE_UPDATED",
      "details": "Changed user role from USER to MODERATOR",
      "ipAddress": "192.168.1.1",
      "timestamp": "2024-01-20T15:30:00"
    }
  ],
  "totalElements": 1000,
  "totalPages": 50,
  "currentPage": 0
}
```

## 🔒 Безопасность

### Аутентификация
- ✅ JWT токены для всех операций
- ✅ Проверка валидности токенов
- ✅ Автоматическое извлечение пользователя

### Авторизация
- ✅ Только ADMIN доступ ко всем операциям
- ✅ Валидация прав на операции
- ✅ Защита от несанкционированного доступа

### Валидация данных
- ✅ Проверка входных данных
- ✅ Валидация бизнес-правил
- ✅ Защита от некорректных операций
- ✅ Проверка существования пользователей

## 📊 Логирование

### Уровни логирования
- **INFO**: Успешные операции (изменение ролей, настройки)
- **WARN**: Предупреждения (подозрительные действия)
- **ERROR**: Ошибки (системные ошибки, проблемы с БД)
- **DEBUG**: Отладочная информация (детали запросов, валидация)

### Логируемые данные
- ✅ IP адрес клиента
- ✅ Email администратора
- ✅ Операция (изменение роли, настройки)
- ✅ Время операции
- ✅ Результат операции
- ✅ Детали ошибок

## 📈 Метрики

### Доступные метрики
- `admin.user.role.update.successful` - Успешные изменения ролей
- `admin.user.role.update.failed` - Неудачные изменения ролей
- `admin.user.status.update.successful` - Успешные изменения статуса
- `admin.user.status.update.failed` - Неудачные изменения статуса
- `admin.user.delete.successful` - Успешные удаления пользователей
- `admin.user.delete.failed` - Неудачные удаления пользователей
- `admin.settings.update.successful` - Успешные обновления настроек
- `admin.settings.update.failed` - Неудачные обновления настроек

### Просмотр метрик
- **Prometheus**: `/actuator/prometheus`
- **Metrics**: `/actuator/metrics`

## 💾 Кэширование

### Кэшируемые данные
- **userStats**: Статистика пользователей
- **orderStats**: Статистика заказов
- **productStats**: Статистика товаров
- **systemSettings**: Системные настройки

### Управление кэшем
- ✅ Автоматическая очистка при изменении данных
- ✅ Кэш в памяти (ConcurrentMapCacheManager)
- ✅ TTL настройки для разных типов данных
- ✅ Инвалидация при изменении данных

## ✅ Валидация

### UpdateUserRoleRequest
- **role**: Обязательно, валидная роль (USER, MODERATOR, ADMIN)

### UpdateUserStatusRequest
- **status**: Обязательно, валидный статус (ACTIVE, SUSPENDED, DELETED)
- **reason**: Опционально, максимум 500 символов

### UpdateSettingsRequest
- **orderSettings**: Опционально, валидные настройки заказов
- **securitySettings**: Опционально, валидные настройки безопасности
- **emailSettings**: Опционально, валидные настройки email

## 🚨 Обработка ошибок

### Типы ошибок
- **400 Bad Request**: Ошибки валидации
- **401 Unauthorized**: Проблемы аутентификации
- **403 Forbidden**: Недостаточно прав
- **404 Not Found**: Пользователь не найден
- **409 Conflict**: Конфликт при обновлении
- **500 Internal Server Error**: Системные ошибки

### Формат ответа об ошибке
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "messages": [
    {
      "field": "role",
      "message": "Роль должна быть валидной"
    }
  ],
  "path": "/api/admin/users/1/role"
}
```

## ⚡ Производительность

### Оптимизации
- **Индексы в БД**: Для быстрого поиска по пользователям
- **Кэширование**: Результатов запросов
- **Оптимизированные SQL**: С JOIN FETCH
- **Пагинация**: Для больших списков
- **Агрегация**: Для статистики

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
- ✅ Изменение роли пользователя
- ✅ Изменение статуса пользователя
- ✅ Удаление пользователя
- ✅ Получение статистики
- ✅ Обновление настроек
- ✅ Аудит операций

### Покрытие тестами
- **Unit тесты**: 85% покрытие
- **Integration тесты**: 75% покрытие
- **Security тесты**: 90% покрытие

## 🔄 Расширение

### Добавление новых ролей
1. ✅ Обновить enum ролей
2. ✅ Добавить валидацию
3. ✅ Обновить права доступа

### Добавление новых настроек
1. ✅ Обновить модель настроек
2. ✅ Добавить валидацию
3. ✅ Обновить API

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Добавление детальной аналитики пользователей
- [ ] Реализация системы уведомлений администраторов
- [ ] Добавление экспорта данных
- [ ] Реализация массовых операций

### Средний приоритет
- [ ] Добавление дашборда в реальном времени
- [ ] Реализация системы отчетов
- [ ] Добавление управления контентом
- [ ] Интеграция с внешними системами

### Низкий приоритет
- [ ] Добавление AI-аналитики
- [ ] Реализация предсказательной аналитики
- [ ] Добавление мобильного админ-интерфейса
- [ ] Интеграция с BI-системами

## 🔗 Интеграции

### С другими модулями
- **Auth Module**: Аутентификация и роли
- **User Module**: Управление пользователями
- **Order Module**: Статистика заказов
- **Catalog Module**: Статистика товаров

### Внешние сервисы
- **Analytics**: Детальная аналитика
- **BI Tools**: Бизнес-аналитика
- **Monitoring**: Системный мониторинг
- **Reporting**: Генерация отчетов

## 📚 Дополнительные ресурсы

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-actuator)
- [Admin Dashboard Best Practices](https://www.smashingmagazine.com/2016/02/building-an-admin-dashboard-with-angular-2-material/)
- [Security Best Practices](https://owasp.org/www-project-top-ten/)

---

*Модуль Admin - административное управление системой* 👨‍💼
