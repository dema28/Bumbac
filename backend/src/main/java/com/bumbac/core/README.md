# 🛠️ Модуль Core

Модуль общей инфраструктуры системы Bumbac, обеспечивающий базовую функциональность для всех остальных модулей.

## 📋 Описание

Модуль Core предоставляет общую инфраструктуру для всей системы:

- ✅ **Конфигурации** (Security, Swagger, Cache, CORS)
- ✅ **Безопасность** (JWT, Rate Limiting, CORS)
- ✅ **Обработка ошибок** (Global Exception Handler)
- ✅ **Утилиты** и общие компоненты
- ✅ **DTO** и общие структуры данных
- ✅ **Валидация** на уровне инфраструктуры
- ✅ **Мониторинг** и метрики
- ✅ **Логирование** централизованное
- ✅ **Документация** API (Swagger/OpenAPI)

## 🏗️ Архитектура

```
core/
├── config/              # Конфигурации Spring Boot
├── security/            # Компоненты безопасности
├── exception/           # Обработка исключений
├── dto/                 # Общие DTO
├── utils/               # Утилиты и хелперы
└── README.md            # Документация
```

## 🔧 Конфигурации

### SecurityConfig
**Основная конфигурация безопасности**

- ✅ JWT аутентификация
- ✅ Авторизация по ролям
- ✅ Rate limiting
- ✅ CORS настройки
- ✅ CSRF защита (отключена для REST API)

**Основные настройки:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // JWT фильтр
    // Rate limiting фильтры
    // CORS конфигурация
    // Метод-уровневая безопасность
}
```

### SwaggerConfig
**Конфигурация API документации**

- ✅ OpenAPI 3.0 спецификация
- ✅ Swagger UI интерфейс
- ✅ Автоматическая генерация документации
- ✅ Кастомные аннотации

**Основные настройки:**
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Bumbac API")
                .version("1.0")
                .description("API для интернет-магазина пряжи"));
    }
}
```

### CacheConfig
**Конфигурация кэширования**

- ✅ In-memory кэширование
- ✅ TTL настройки
- ✅ Автоматическая инвалидация
- ✅ Мониторинг кэша

### CorsConfig
**Конфигурация CORS**

- ✅ Разрешенные origins
- ✅ Разрешенные методы
- ✅ Разрешенные заголовки
- ✅ Credentials поддержка

### RateLimiterConfig
**Конфигурация rate limiting**

- ✅ Bucket4j интеграция
- ✅ Настройки лимитов
- ✅ Кастомные фильтры
- ✅ Мониторинг лимитов

### WebConfig
**Общая конфигурация веб-приложения**

- ✅ CORS настройки
- ✅ Interceptors
- ✅ Message converters
- ✅ Exception resolvers

## 🔒 Безопасность

### JWT Аутентификация
- **Алгоритм**: HS256
- **Время жизни**: 24 часа (настраивается)
- **Refresh токены**: Поддержка обновления
- **Stateless**: Без сессий

### Rate Limiting
- **Регистрация**: 3 запроса/минуту с IP
- **Вход**: 5 попыток/минуту с IP
- **Общие запросы**: 100 запросов/минуту с IP
- **Burst capacity**: Настраивается

### Авторизация
- **Роли**: USER, MODERATOR, ADMIN
- **Метод-уровневая**: `@PreAuthorize`
- **URL-уровневая**: Конфигурация в SecurityConfig
- **Кастомные**: AccessDeniedHandler

### CORS
- **Разрешенные origins**: Настраиваются
- **Методы**: GET, POST, PUT, DELETE, OPTIONS
- **Заголовки**: Authorization, Content-Type
- **Credentials**: Поддерживаются

## 🚨 Обработка ошибок

### GlobalExceptionHandler
**Централизованная обработка исключений**

**Поддерживаемые типы:**
- `BaseException` - базовые бизнес-исключения
- `RuntimeException` - общие runtime ошибки
- `AccessDeniedException` - ошибки доступа
- `UserNotFoundException` - пользователь не найден
- `ResourceNotFoundException` - ресурс не найден
- `MethodArgumentNotValidException` - ошибки валидации DTO
- `ConstraintViolationException` - ошибки валидации сущностей
- `ResponseStatusException` - HTTP статус исключения
- `JwtException` - ошибки JWT токенов
- `BadCredentialsException` - неверные учетные данные

**Форматы ответов:**
1. **Легаси формат** (для совместимости):
   ```json
   {
     "timestamp": "2025-07-23T20:05:00",
     "status": 404,
     "error": "Not Found",
     "message": "Пользователь не найден",
     "path": "/api/admin/users/999"
   }
   ```

2. **Новый единый формат**:
   ```json
   {
     "timestamp": "2025-07-23T20:05:00",
     "status": 404,
     "error": "Not Found",
     "message": "Пользователь не найден",
     "details": {
       "userId": 999,
       "suggestion": "Проверьте корректность ID"
     },
     "path": "/api/admin/users/999"
   }
   ```

### Кастомные исключения
- `BaseException` - базовая бизнес-исключение
- `UserNotFoundException` - пользователь не найден
- `ResourceNotFoundException` - ресурс не найден
- `ValidationException` - ошибка валидации
- `BusinessLogicException` - ошибка бизнес-логики

## 📊 Мониторинг

### Actuator Endpoints
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`
- **Loggers**: `/actuator/loggers`
- **Environment**: `/actuator/env`

### Метрики
- **JVM метрики**: Память, CPU, GC
- **HTTP метрики**: Запросы, ответы, ошибки
- **База данных**: Connection pool, запросы
- **Кэш**: Hit/miss ratio, размер
- **Кастомные**: Бизнес-метрики

### Логирование
- **Фреймворк**: Logback
- **Формат**: JSON с structured logging
- **Уровни**: DEBUG, INFO, WARN, ERROR
- **Ротация**: По размеру и времени
- **Агрегация**: Централизованное логирование

## 💾 Кэширование

### Стратегии кэширования
- **In-memory**: ConcurrentMapCacheManager
- **TTL**: Настраиваемое время жизни
- **Инвалидация**: Автоматическая при изменениях
- **Мониторинг**: Метрики использования

### Кэшируемые данные
- **Пользователи**: По email и ID
- **Товары**: Списки и детали
- **Настройки**: Системные конфигурации
- **Статистика**: Агрегированные данные

## ✅ Валидация

### Bean Validation
- **Аннотации**: @NotNull, @Size, @Email, @Pattern
- **Кастомные**: Валидаторы для бизнес-правил
- **Группы**: Разные валидации для разных сценариев
- **Сообщения**: Локализованные сообщения об ошибках

### Кастомные валидаторы
- **PhoneValidator**: Валидация телефонных номеров
- **PasswordValidator**: Сложность паролей
- **FileValidator**: Типы и размеры файлов
- **BusinessRuleValidator**: Бизнес-правила

## 🔧 Утилиты

### DateUtil
**Утилиты для работы с датами**

- ✅ Форматирование дат
- ✅ Парсинг дат
- ✅ Вычисления с датами
- ✅ Временные зоны

### ValidationUtil
**Утилиты валидации**

- ✅ Проверка email
- ✅ Проверка телефона
- ✅ Проверка пароля
- ✅ Бизнес-правила

### SecurityUtil
**Утилиты безопасности**

- ✅ Генерация токенов
- ✅ Хеширование паролей
- ✅ Проверка прав доступа
- ✅ Аудит операций

## 📈 Производительность

### Оптимизации
- **Connection Pooling**: HikariCP
- **Кэширование**: На всех уровнях
- **Lazy Loading**: Для связанных данных
- **Batch Processing**: Для массовых операций
- **Async Processing**: Для тяжелых операций

### Мониторинг производительности
- **Response Time**: Среднее время ответа
- **Throughput**: Запросов в секунду
- **Error Rate**: Процент ошибок
- **Resource Usage**: CPU, Memory, Disk

## 🧪 Тестирование

### Тестовые конфигурации
- **Test Security**: Отключенная безопасность
- **Test Database**: H2 in-memory
- **Test Cache**: Простой кэш
- **Test Metrics**: Отключенные метрики

### Тестовые утилиты
- **TestDataBuilder**: Создание тестовых данных
- **TestSecurityContext**: Контекст безопасности
- **TestWebClient**: HTTP клиент для тестов
- **TestAssertions**: Кастомные assertions

## 🔄 Расширение

### Добавление новых конфигураций
1. ✅ Создать класс конфигурации
2. ✅ Добавить @Configuration аннотацию
3. ✅ Настроить бины
4. ✅ Добавить документацию

### Добавление новых исключений
1. ✅ Создать класс исключения
2. ✅ Добавить в GlobalExceptionHandler
3. ✅ Настроить обработку
4. ✅ Добавить тесты

### Добавление новых утилит
1. ✅ Создать класс утилит
2. ✅ Добавить статические методы
3. ✅ Добавить валидацию
4. ✅ Добавить тесты

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Добавление Redis кэширования
- [ ] Реализация distributed rate limiting
- [ ] Добавление circuit breaker
- [ ] Улучшение мониторинга

### Средний приоритет
- [ ] Добавление health checks
- [ ] Реализация graceful shutdown
- [ ] Добавление feature flags
- [ ] Интеграция с APM

### Низкий приоритет
- [ ] Добавление distributed tracing
- [ ] Реализация chaos engineering
- [ ] Добавление performance testing
- [ ] Интеграция с внешними системами

## 🔗 Интеграции

### С другими модулями
- **Все модули**: Используют core инфраструктуру
- **Auth Module**: JWT и безопасность
- **Admin Module**: Мониторинг и метрики
- **User Module**: Валидация и утилиты

### Внешние сервисы
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack, Fluentd
- **APM**: New Relic, Datadog
- **Security**: OWASP, Security Headers

## 📚 Дополнительные ресурсы

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Cache Documentation](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Micrometer Documentation](https://micrometer.io/docs)

---

*Модуль Core - основа инфраструктуры системы* 🛠️
