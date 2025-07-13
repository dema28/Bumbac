# 🧵 Bumbac — Backend

Java 17 + Spring Boot 3 REST API для мультиязычного интернет-магазина пряжи **Bumbac.md**

![Java](https://img.shields.io/badge/Java-17%2B-informational?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.x-brightgreen?logo=spring-boot)
![License](https://img.shields.io/badge/license-Apache_2.0-blue)

---

## 🚀 Возможности

| Модуль | Возможности |
|--------|-------------|
| Каталог | Ярн, Цвета, Категории, Мультиязычные переводы |
| Пользователь | Регистрация, Вход, JWT, Роли `USER`, `ADMIN`, `CONTENT_MANAGER` |
| Корзина | Добавление/удаление товаров, автоматическая инициализация |
| Заказы | Фиксация цены, Создание заказа, Статусы, История |
| Возвраты | Частичный возврат с указанием причины, количества, цвета |
| Избранное | Добавление/удаление любимых товаров |
| Почта | Поддержка SMTP через Mailtrap/Brevo |
| Загрузка файлов | Поддержка аватаров и других ресурсов |
| Документация | Swagger UI + Actuator Endpoints |

---

## 🧰 Технологии

- Java 17, Spring Boot 3.2.x
- Maven (`mvn`)
- Spring Data JPA (Hibernate)
- Spring Security + JWT (`jjwt`)
- MySQL 8 (с поддержкой PostgreSQL)
- Lombok, Spring Validation
- Swagger/OpenAPI (`springdoc-openapi`)
- SMTP (Mailtrap или Brevo)
- Docker-готовность

---

## ⚙️ Запуск

### 1 — Клонировать репозиторий

```bash
git clone https://github.com/yourname/bumbac-backend.git
cd bumbac-backend
```

### 2 — Создать базу данных

```sql
CREATE DATABASE yarn_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON yarn_store.* TO 'bumbac_user'@'localhost' IDENTIFIED BY 'secret';
```

### 3 — Настроить `.env`

```env
DB_HOST=localhost
DB_NAME=yarn_store
DB_USER=bumbac_user
DB_PASSWORD=secret

JWT_SECRET=SomeSuperSecret32CharsString
SMTP_HOST=smtp.mailtrap.io
SMTP_PORT=587
SMTP_USER=your-user
SMTP_PASS=your-pass
WAREHOUSE_EMAIL=orders@bumbac.md
```

### 4 — Собрать и запустить

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📚 API Документация

| URL | Назначение |
|-----|------------|
| `/swagger-ui.html` | Swagger UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/actuator/health` | Health-check |

---

## 🧾 Примеры запросов

### 🔐 Регистрация и вход

```http
POST /api/auth/register
{
  "email": "user@mail.com",
  "password": "12345678"
}

POST /api/auth/login
{
  "email": "user@mail.com",
  "password": "12345678"
}
→ Bearer Token
```

### 🛍 Корзина

```http
POST /api/cart
Authorization: Bearer <jwt>
{
  "yarnId": 12,
  "quantity": 3
}

GET /api/cart
DELETE /api/cart/clear
```

### 📦 Заказ

```http
POST /api/orders
Authorization: Bearer <jwt>
→ Текущий состав корзины будет зафиксирован как заказ
```

### ↩️ Возврат

```http
POST /api/returns
Authorization: Bearer <jwt>
{
  "orderId": 1,
  "refundAmountCzk": 120.50,
  "items": [
    {
      "colorId": 4,
      "quantity": 2,
      "reason": "Неправильный цвет"
    }
  ]
}
```

---

## 📁 Структура проекта

```
src/
├── main/
│   ├── java/com/bumbac/
│   │   ├── auth/            # регистрация, вход, JWT
│   │   ├── cart/            # корзина
│   │   ├── catalog/         # yarn, color, категория
│   │   ├── order/           # заказы, возвраты
│   │   ├── user/            # профиль, роли
│   │   ├── common/          # конфигурации, утилиты
│   └── resources/
│       ├── application.yml
│       ├── i18n/messages_ru.properties
│       ├── templates/email.html
└── test/
```

---

## 🧪 Тесты

```bash
mvn test
```

---

## 🐳 Docker

```bash
docker build -t bumbac-backend .
docker run --env-file .env -p 8080:8080 bumbac-backend
```

---

## 📬 Контакты

Проект разрабатывается для сайта [https://bumbac](https://bumbac.md)  
Если нашли баг или хотите помочь — welcome в issues и pull requests!
