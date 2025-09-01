# 🔗 Модуль Shared

Модуль общих компонентов и утилит, используемых всеми модулями системы Bumbac.

## 📋 Описание

Модуль Shared содержит общие компоненты:

- ✅ **Общие DTO** (ApiResponse, ErrorResponse)
- ✅ **Утилиты** (DateUtil, ValidationUtil)
- ✅ **Исключения** (BaseException, ResourceNotFoundException)
- ✅ **Конфигурации** (общие настройки)
- ✅ **Аннотации** (кастомные аннотации)
- ✅ **Константы** (общие константы)
- ✅ **Хелперы** (общие хелперы)
- ✅ **Интерфейсы** (общие интерфейсы)

## 🏗️ Архитектура

```
shared/
├── dto/                 # Общие Data Transfer Objects
│   ├── ApiResponse.java
│   └── ErrorResponse.java
├── utils/               # Утилиты и хелперы
│   └── DateUtil.java
├── exceptions/          # Общие исключения
│   ├── BaseException.java
│   └── ResourceNotFoundException.java
├── config/              # Общие конфигурации
│   └── WebConfig.java
├── constants/           # Общие константы
├── annotations/         # Кастомные аннотации
├── interfaces/          # Общие интерфейсы
└── README.md            # Документация
```

## 📦 Общие DTO

### ApiResponse
**Стандартный ответ API**

- ✅ **Success**: Статус операции
- ✅ **Data**: Данные ответа
- ✅ **Message**: Сообщение
- ✅ **Timestamp**: Временная метка
- ✅ **Path**: Путь запроса

**Пример использования:**
```java
@GetMapping("/users")
public ApiResponse<List<UserDTO>> getUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ApiResponse.success(users, "Users retrieved successfully");
}
```

### ErrorResponse
**Стандартный ответ об ошибке**

- ✅ **Timestamp**: Время ошибки
- ✅ **Status**: HTTP статус
- ✅ **Error**: Тип ошибки
- ✅ **Message**: Сообщение об ошибке
- ✅ **Path**: Путь запроса
- ✅ **Details**: Дополнительные детали

**Пример использования:**
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFound(
    ResourceNotFoundException ex, HttpServletRequest request) {
    
    ErrorResponse error = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
```

## 🛠️ Утилиты

### DateUtil
**Утилиты для работы с датами**

**Основные методы:**
- ✅ `formatDate(LocalDateTime date, String pattern)` - Форматирование даты
- ✅ `parseDate(String dateString, String pattern)` - Парсинг даты
- ✅ `addDays(LocalDateTime date, long days)` - Добавление дней
- ✅ `isBetween(LocalDateTime date, LocalDateTime start, LocalDateTime end)` - Проверка диапазона
- ✅ `getCurrentTimestamp()` - Текущая временная метка

**Пример использования:**
```java
LocalDateTime now = DateUtil.getCurrentTimestamp();
String formatted = DateUtil.formatDate(now, "yyyy-MM-dd HH:mm:ss");
LocalDateTime future = DateUtil.addDays(now, 7);
```

### ValidationUtil
**Утилиты валидации**

**Основные методы:**
- ✅ `isValidEmail(String email)` - Проверка email
- ✅ `isValidPhone(String phone)` - Проверка телефона
- ✅ `isValidPassword(String password)` - Проверка пароля
- ✅ `isValidUUID(String uuid)` - Проверка UUID
- ✅ `sanitizeString(String input)` - Очистка строки

**Пример использования:**
```java
if (!ValidationUtil.isValidEmail(email)) {
    throw new ValidationException("Invalid email format");
}

String sanitized = ValidationUtil.sanitizeString(userInput);
```

### SecurityUtil
**Утилиты безопасности**

**Основные методы:**
- ✅ `generateToken()` - Генерация токена
- ✅ `hashPassword(String password)` - Хеширование пароля
- ✅ `verifyPassword(String password, String hash)` - Проверка пароля
- ✅ `generateRandomString(int length)` - Генерация случайной строки
- ✅ `encodeBase64(String input)` - Base64 кодирование

**Пример использования:**
```java
String token = SecurityUtil.generateToken();
String hashedPassword = SecurityUtil.hashPassword(password);
boolean isValid = SecurityUtil.verifyPassword(password, hashedPassword);
```

## 🚨 Исключения

### BaseException
**Базовая бизнес-исключение**

- ✅ **Message**: Сообщение об ошибке
- ✅ **Code**: Код ошибки
- ✅ **Details**: Дополнительные детали
- ✅ **Cause**: Причина исключения

**Пример использования:**
```java
public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId, "USER_NOT_FOUND");
    }
}
```

### ResourceNotFoundException
**Исключение для отсутствующих ресурсов**

- ✅ **Resource**: Тип ресурса
- ✅ **Identifier**: Идентификатор ресурса
- ✅ **Message**: Сообщение об ошибке

**Пример использования:**
```java
public class UserService {
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
```

## ⚙️ Конфигурации

### WebConfig
**Общая конфигурация веб-приложения**

- ✅ **CORS настройки**: Разрешенные origins, методы, заголовки
- ✅ **Interceptors**: Кастомные интерцепторы
- ✅ **Message converters**: Конвертеры сообщений
- ✅ **Exception resolvers**: Обработчики исключений

**Пример конфигурации:**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }
}
```

## 📋 Константы

### ApiConstants
**Константы API**

- ✅ **API_VERSION**: Версия API
- ✅ **DEFAULT_PAGE_SIZE**: Размер страницы по умолчанию
- ✅ **MAX_PAGE_SIZE**: Максимальный размер страницы
- ✅ **DEFAULT_SORT**: Сортировка по умолчанию

### ValidationConstants
**Константы валидации**

- ✅ **MIN_PASSWORD_LENGTH**: Минимальная длина пароля
- ✅ **MAX_PASSWORD_LENGTH**: Максимальная длина пароля
- ✅ **MIN_NAME_LENGTH**: Минимальная длина имени
- ✅ **MAX_NAME_LENGTH**: Максимальная длина имени

### SecurityConstants
**Константы безопасности**

- ✅ **JWT_EXPIRATION**: Время жизни JWT токена
- ✅ **REFRESH_TOKEN_EXPIRATION**: Время жизни refresh токена
- ✅ **BCRYPT_ROUNDS**: Количество раундов BCrypt
- ✅ **RATE_LIMIT_PER_MINUTE**: Лимит запросов в минуту

## 🏷️ Аннотации

### @Audited
**Аннотация для аудита операций**

- ✅ **Action**: Тип действия
- ✅ **Resource**: Тип ресурса
- ✅ **Description**: Описание операции

**Пример использования:**
```java
@Audited(action = "CREATE", resource = "USER", description = "User registration")
public User registerUser(RegisterRequest request) {
    // Реализация
}
```

### @Cacheable
**Аннотация для кэширования**

- ✅ **Key**: Ключ кэша
- ✅ **TTL**: Время жизни кэша
- ✅ **Condition**: Условие кэширования

**Пример использования:**
```java
@Cacheable(key = "#id", ttl = 3600)
public User findById(Long id) {
    return userRepository.findById(id).orElse(null);
}
```

## 🔧 Интерфейсы

### CrudService
**Общий интерфейс для CRUD операций**

```java
public interface CrudService<T, ID, DTO> {
    List<DTO> findAll();
    DTO findById(ID id);
    DTO create(DTO dto);
    DTO update(ID id, DTO dto);
    void delete(ID id);
}
```

### Auditable
**Интерфейс для аудируемых сущностей**

```java
public interface Auditable {
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
}
```

### Versionable
**Интерфейс для версионируемых сущностей**

```java
public interface Versionable {
    Long getVersion();
    void incrementVersion();
}
```

## 🧪 Тестирование

### TestUtils
**Утилиты для тестирования**

- ✅ **TestDataBuilder**: Создание тестовых данных
- ✅ **MockDataGenerator**: Генерация мок-данных
- ✅ **TestAssertions**: Кастомные assertions
- ✅ **TestConstants**: Константы для тестов

### TestConfig
**Конфигурация для тестов**

- ✅ **TestDatabase**: H2 in-memory база данных
- ✅ **TestSecurity**: Отключенная безопасность
- ✅ **TestCache**: Простой кэш
- ✅ **TestMetrics**: Отключенные метрики

## 🔄 Расширение

### Добавление новой утилиты
1. ✅ Создать класс утилиты
2. ✅ Добавить статические методы
3. ✅ Добавить валидацию
4. ✅ Добавить тесты

### Добавление нового исключения
1. ✅ Создать класс исключения
2. ✅ Наследовать от BaseException
3. ✅ Добавить конструкторы
4. ✅ Добавить тесты

### Добавление новой константы
1. ✅ Добавить в соответствующий класс констант
2. ✅ Добавить документацию
3. ✅ Обновить тесты
4. ✅ Обновить документацию

## 🚧 TODO и улучшения

### Высокий приоритет
- [ ] Добавление большего количества утилит
- [ ] Улучшение обработки исключений
- [ ] Добавление валидаторов
- [ ] Система логирования

### Средний приоритет
- [ ] Добавление кастомных аннотаций
- [ ] Система метрик
- [ ] Интеграция с внешними сервисами
- [ ] Система кэширования

### Низкий приоритет
- [ ] Система плагинов
- [ ] Интеграция с мониторингом
- [ ] Система событий
- [ ] Система конфигурации

## 🔗 Интеграции

### Внутренние модули
- **Все модули**: Используют shared компоненты
- **Core Module**: Общие конфигурации
- **Auth Module**: Утилиты безопасности
- **Admin Module**: Утилиты валидации

### Внешние библиотеки
- **Apache Commons**: Утилиты
- **Guava**: Дополнительные утилиты
- **Jackson**: JSON обработка
- **Validation API**: Валидация

## 📚 Дополнительные ресурсы

- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
- [Google Guava](https://github.com/google/guava)
- [Jackson Documentation](https://github.com/FasterXML/jackson)
- [Bean Validation](https://beanvalidation.org/)

---

*Модуль Shared - общие компоненты системы* 🔗
