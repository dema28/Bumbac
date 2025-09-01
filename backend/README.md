# 🧵 Bumbac Backend

**Модульная Spring Boot архитектура для интернет-магазина пряжи**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

---

## 📋 Содержание

- [Обзор проекта](#обзор-проекта)
- [Архитектура](#архитектура)
- [Модули](#модули)
- [Технологии](#технологии)
- [Быстрый старт](#быстрый-старт)
- [API Документация](#api-документация)
- [Безопасность](#безопасность)
- [Мониторинг](#мониторинг)
- [Разработка](#разработка)

---

## 🎯 Обзор проекта

Bumbac Backend — это высоконагруженное Spring Boot приложение для интернет-магазина пряжи с модульной архитектурой, обеспечивающее:

- 🔐 **Безопасность**: JWT аутентификация, роли, rate limiting
- 🛒 **E-commerce**: Корзина, заказы, платежи, возвраты
- 📦 **Каталог**: Пряжа, цвета, схемы вязания, медиафайлы
- 👥 **Пользователи**: Профили, избранное, подписки
- 📧 **Коммуникации**: Email рассылки, контакты
- 📊 **Мониторинг**: Метрики, логирование, health checks
- 🚀 **Производительность**: Кэширование, оптимизация запросов

---

## 🏗️ Архитектура

### Модульная структура

```
src/main/java/com/bumbac/
├── core/                    # 🛠️ Общая инфраструктура
│   ├── config/             # Конфигурации (Security, Swagger, Cache)
│   ├── security/           # JWT, фильтры, rate limiting
│   ├── exception/           # Глобальная обработка ошибок
│   ├── dto/                # Общие DTO
│   └── utils/               # Утилиты
├── modules/                # 📦 Бизнес-модули
│   ├── auth/               # Аутентификация и авторизация
│   ├── user/               # Профили пользователей
│   ├── catalog/            # Каталог товаров
│   ├── cart/               # Корзина покупок
│   ├── order/              # Заказы и платежи
│   ├── newsletter/         # Email рассылки
│   ├── contact/            # Контактные формы
│   ├── admin/              # Административные функции
│   ├── pattern/            # Схемы вязания
│   └── media/              # Управление медиафайлами
└── shared/                 # 🔄 Общие компоненты
    ├── entity/             # Общие сущности
    ├── repository/         # Общие репозитории
    ├── service/            # Общие сервисы
    ├── mapper/             # Мапперы
    └── enums/              # Перечисления
```

### Принципы архитектуры

- **Модульность**: Каждый модуль независим и самодостаточен
- **Слабая связанность**: Минимальные зависимости между модулями
- **Высокая когезия**: Связанная функциональность в одном модуле
- **SOLID принципы**: Следование принципам SOLID
- **Clean Architecture**: Разделение на слои (Controller → Service → Repository)

---

## 📦 Модули

### 🔐 Auth Module
**Аутентификация и авторизация**

- JWT токены (access + refresh)
- Регистрация и вход пользователей
- Роли и права доступа (USER, MODERATOR, ADMIN)
- Rate limiting для защиты от атак
- Метрики безопасности
- Кэширование пользователей

**Endpoints:**
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Вход
- `POST /api/auth/refresh` - Обновление токена
- `POST /api/auth/logout` - Выход

### 👤 User Module
**Управление профилями пользователей**

- Профили пользователей
- Избранные товары
- Настройки аккаунта
- История активности

**Endpoints:**
- `GET /api/user/profile` - Получение профиля
- `PUT /api/user/profile` - Обновление профиля
- `GET /api/user/favorites` - Избранное
- `POST /api/user/favorites` - Добавление в избранное

### 🧶 Catalog Module
**Каталог товаров**

- Пряжа и её характеристики
- Цвета и их доступность
- Фильтрация и поиск
- Спецификации для сложных запросов
- Кэширование каталога

**Endpoints:**
- `GET /api/catalog/yarns` - Список пряжи
- `GET /api/catalog/yarns/{id}` - Детали пряжи
- `GET /api/catalog/colors` - Цвета
- `GET /api/catalog/search` - Поиск

### 🛒 Cart Module
**Корзина покупок**

- Добавление/удаление товаров
- Изменение количества
- Автоматическая инициализация
- Очистка корзины
- Валидация товаров

**Endpoints:**
- `GET /api/cart` - Содержимое корзины
- `POST /api/cart` - Добавление товара
- `PUT /api/cart` - Обновление количества
- `DELETE /api/cart` - Очистка корзины

### 📦 Order Module
**Заказы и платежи**

- Создание заказов
- Статусы заказов
- Платежи и их статусы
- Возвраты товаров
- Доставка и отслеживание

**Endpoints:**
- `POST /api/orders` - Создание заказа
- `GET /api/orders` - История заказов
- `GET /api/orders/{id}` - Детали заказа
- `POST /api/returns` - Возврат товара

### 📧 Newsletter Module
**Email рассылки**

- Подписка на рассылки
- Подтверждение email
- Отписка от рассылок
- Управление списками рассылок

**Endpoints:**
- `POST /api/newsletter/subscribe` - Подписка
- `GET /api/newsletter/confirm` - Подтверждение
- `POST /api/newsletter/unsubscribe` - Отписка

### 📞 Contact Module
**Контактные формы**

- Обработка контактных форм
- Загрузка файлов
- Уведомления администраторов
- История обращений

**Endpoints:**
- `POST /api/contact` - Отправка сообщения
- `GET /api/contact` - История сообщений (ADMIN)

### 👨‍💼 Admin Module
**Административные функции**

- Управление пользователями
- Назначение ролей
- Статистика и аналитика
- Системные настройки

**Endpoints:**
- `GET /api/admin/users` - Список пользователей
- `PUT /api/admin/users/{id}/role` - Изменение роли
- `DELETE /api/admin/users/{id}` - Удаление пользователя

### 📖 Pattern Module
**Схемы вязания**

- Схемы вязания
- Мультиязычные описания
- Категории сложности
- Связь с пряжей

**Endpoints:**
- `GET /api/patterns` - Список схем
- `GET /api/patterns/{id}` - Детали схемы

### 🖼️ Media Module
**Управление медиафайлами**

- Загрузка изображений
- Обработка файлов
- Связь с товарами
- CDN интеграция

**Endpoints:**
- `POST /api/media/upload` - Загрузка файла
- `GET /api/media/{id}` - Получение файла

---

## 🛠️ Технологии

### Backend
- **Java 17** - Современная версия Java
- **Spring Boot 3.2.4** - Основной фреймворк
- **Spring Security** - Безопасность и аутентификация
- **Spring Data JPA** - Работа с базой данных
- **Spring Cache** - Кэширование
- **Spring Validation** - Валидация данных

### База данных
- **MySQL 8.0** - Основная база данных
- **Hibernate** - ORM
- **Flyway** - Миграции (планируется)

### Безопасность
- **JWT** - JSON Web Tokens
- **BCrypt** - Хеширование паролей
- **Rate Limiting** - Защита от атак
- **CORS** - Cross-Origin Resource Sharing

### Документация и мониторинг
- **Swagger/OpenAPI** - API документация
- **Spring Actuator** - Мониторинг
- **Micrometer** - Метрики
- **Logback** - Логирование

### Дополнительные библиотеки
- **Lombok** - Уменьшение boilerplate кода
- **MapStruct** - Маппинг объектов
- **Bucket4j** - Rate limiting
- **Dotenv** - Переменные окружения

---

## 🚀 Быстрый старт

### Предварительные требования

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Git

### Установка и запуск

1. **Клонирование репозитория**
```bash
   git clone https://github.com/your-org/bumbac-backend.git
cd bumbac-backend
```

2. **Настройка базы данных**
```sql
CREATE DATABASE yarn_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'bumbac_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON yarn_store.* TO 'bumbac_user'@'localhost';
   ```

3. **Настройка переменных окружения**
   ```bash
   cp .env.example .env
   # Отредактируйте .env файл
   ```

4. **Запуск приложения**
```bash
mvn spring-boot:run
```

5. **Проверка работоспособности**
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

Подробные инструкции по установке см. в [SETUP.md](SETUP.md)

---

## 📚 API Документация

### Swagger UI
Интерактивная документация доступна по адресу:
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Основные endpoints

#### Аутентификация
```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

#### Каталог
```http
GET /api/catalog/yarns
GET /api/catalog/yarns/{id}
GET /api/catalog/colors
GET /api/catalog/search?q=wool
```

#### Корзина
```http
GET /api/cart
POST /api/cart
PUT /api/cart
DELETE /api/cart
```

#### Заказы
```http
POST /api/orders
GET /api/orders
GET /api/orders/{id}
POST /api/returns
```

#### Пользователи
```http
GET /api/user/profile
PUT /api/user/profile
GET /api/user/favorites
POST /api/user/favorites
```

---

## 🔒 Безопасность

### Аутентификация
- **JWT токены** с временем жизни 24 часа
- **Refresh токены** для обновления access токенов
- **BCrypt** хеширование паролей
- **Stateless** сессии

### Авторизация
- **Роли**: USER, MODERATOR, ADMIN
- **Метод-уровневая безопасность** с `@PreAuthorize`
- **Защищенные endpoints** требуют аутентификации

### Защита от атак
- **Rate limiting**: 3 регистрации/мин, 5 входов/мин
- **CORS** настройки
- **CSRF** защита (отключена для REST API)
- **Input validation** на всех уровнях

### Мониторинг безопасности
- **Логирование** всех операций безопасности
- **Метрики** попыток входа/регистрации
- **Алерты** при подозрительной активности

---

## 📊 Мониторинг

### Health Checks
- **URL**: http://localhost:8080/actuator/health
- **Проверка**: База данных, кэш, внешние сервисы

### Метрики
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Metrics**: http://localhost:8080/actuator/metrics

### Логирование
- **Файл**: `logs/bumbac-app.log`
- **Уровни**: INFO, WARN, ERROR, DEBUG
- **Ротация**: 10MB файлы, 30 дней хранения

### Мониторинг в продакшене
- **Prometheus** + **Grafana** для метрик
- **ELK Stack** для логов
- **AlertManager** для уведомлений

---

## 👨‍💻 Разработка

### Структура проекта
```
src/
├── main/
│   ├── java/com/bumbac/
│   │   ├── core/           # Общая инфраструктура
│   │   ├── modules/         # Бизнес-модули
│   │   └── shared/          # Общие компоненты
│   └── resources/
│       ├── application.yml  # Конфигурация
│       └── db/migration/    # Миграции БД
└── test/
    └── java/com/bumbac/
        └── modules/         # Тесты модулей
```

### Стиль кода
- **Java Code Style**: Google Java Style
- **Naming**: camelCase для методов, PascalCase для классов
- **Documentation**: JavaDoc для публичных методов
- **Testing**: TestNG для unit и integration тестов

### Git Workflow
- **Main branch**: Стабильная версия
- **Feature branches**: Для новых функций
- **Pull requests**: Code review обязателен
- **Conventional Commits**: Стандартизированные сообщения

### Тестирование
```bash
# Запуск всех тестов
mvn test

# Запуск конкретного модуля
mvn test -Dtest=AuthModuleTest

# Покрытие кода
mvn jacoco:report
```

### Сборка и деплой
```bash
# Сборка JAR
mvn clean package

# Запуск в Docker
docker build -t bumbac-backend .
docker run -p 8080:8080 bumbac-backend

# Kubernetes deployment
kubectl apply -f k8s/
```

---

## 📈 Производительность

### Оптимизации
- **Кэширование**: Redis для пользователей и каталога
- **Connection Pooling**: HikariCP для БД
- **Lazy Loading**: Для связанных сущностей
- **Pagination**: Для больших списков
- **Compression**: Gzip для ответов

### Мониторинг производительности
- **Response Time**: Среднее время ответа
- **Throughput**: Запросов в секунду
- **Error Rate**: Процент ошибок
- **Resource Usage**: CPU, Memory, Disk

---

## 🤝 Вклад в проект

### Как внести вклад
1. Fork репозитория
2. Создайте feature branch
3. Внесите изменения
4. Добавьте тесты
5. Создайте Pull Request

### Требования к PR
- [ ] Код соответствует стилю проекта
- [ ] Добавлены тесты
- [ ] Обновлена документация
- [ ] Все тесты проходят
- [ ] Code review пройден

### Отчеты об ошибках
- Используйте GitHub Issues
- Опишите проблему подробно
- Приложите логи и скриншоты
- Укажите версию и окружение

---

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

---

## 📞 Контакты

- **Email**: dema28ster@gmail.com
- **Website**:
- **Issues**: https://github.com/your-org/bumbac-backend/issues
- **Documentation**: 

---

## 🙏 Благодарности

- Spring Boot Team за отличный фреймворк
- MySQL Team за надежную БД
- Open Source сообществу за библиотеки
- Всем контрибьюторам проекта

---

*Сделано с ❤️ командой Bumbac* 
