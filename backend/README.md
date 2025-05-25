# 🧵 Bumbac.md — Backend

Java 17 + Spring Boot 3 REST API для мультиязычного интернет-магазина пряжи и текстиля **Bumbac.md**.

![Java](https://img.shields.io/badge/Java-17%2B-informational?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.x-brightgreen?logo=spring-boot)
![License](https://img.shields.io/badge/license-Apache_2.0-blue)

---

## 🚀 Возможности

| Блок | Что есть |
|------|----------|
| Каталог | товары, категории, цвет, тех. характеристики, мультиязык (ru/ro/en) |
| Поиск/фильтры | цвет, цена, категория, язык |
| Пользователи | регистрация, вход, JWT, проверка email |
| Роли | `USER`, `ADMIN`, `CONTENT_MANAGER` |
| Заказы | проверка остатков, блокировка, фиксация цены |
| Почта | Mailtrap / SMTP (warehouse email) |
| Избранное | добавить/убрать товар в `favorites` |
| Аватар & загрузка файлов | /api/upload |
| Swagger UI | `/swagger-ui.html` |
| Actuator | `/actuator/health`, `/actuator/info` |

---

## 🧰 Технологии

* **Java 17**, Maven Wrapper  
* **Spring Boot 3.2**: Web, Data JPA, Security, Validation, Mail, Actuator  
* **MySQL 8** (можно PostgreSQL другим профилем)  
* **JWT** (`jjwt 0.12.5`)  
* **Lombok 1.18.32**  
* **Springdoc OpenAPI 2.3** (Swagger UI)

---

## 🛠 Запуск проекта

### 1 — Клонировать

```bash
git clone https://github.com/yourname/bumbac-backend.git
cd bumbac-backend
```

### 2 — Создать базу MySQL

```sql
CREATE DATABASE bumbac_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'bumbac_user'@'localhost' IDENTIFIED BY 'secret';
GRANT ALL PRIVILEGES ON bumbac_db.* TO 'bumbac_user'@'localhost';
```

### 3 — Переменные окружения

```bash
# БД
export DB_HOST=localhost
export DB_NAME=bumbac_db
export DB_USER=bumbac_user
export DB_PASSWORD=secret

# JWT
export JWT_SECRET="ChangeMeSuperSecretKeyWith32CharsMinimum"

# Email (Mailtrap пример)
export SMTP_HOST=smtp.mailtrap.io
export SMTP_PORT=587
export SMTP_USER=<mailtrap_user>
export SMTP_PASS=<mailtrap_pass>
export WAREHOUSE_EMAIL=orders@bumbac.md
```

### 4 — Собрать и запустить

```bash
./mvnw clean package
./mvnw spring-boot:run        # профиль mysql активируется автоматически
```

> Другие профили:  
> `spring-boot:run -Dspring-boot.run.profiles=mysql,mail`  
> `spring-boot:run -Dspring-boot.run.profiles=postgres`

---

## 📚 API Документация

| URL | Описание |
|-----|----------|
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |
| `http://localhost:8080/actuator/health` | Health-check |

---

## 🔑 Примеры запросов

### Аутентификация

```http
POST /api/auth/register
{
  "email": "user@mail.com",
  "password": "123456",
  "fullName": "Demo User"
}

POST /api/auth/login
{
  "email": "user@mail.com",
  "password": "123456"
}
→ 200 OK
Authorization: Bearer <jwt>
```

### Товары

```http
GET /api/products?lang=ro
GET /api/products/filter?color=Red
GET /api/products/42
```

### Заказ

```http
POST /api/orders
Authorization: Bearer <jwt>
{
  "items": [
    { "product": { "id": 42 }, "quantity": 3 }
  ],
  "recipientName": "Ion Popescu",
  "contactPerson": "Ion P.",
  "phoneNumber": "+37360000000",
  "email": "ion@example.com",
  "country": "MD",
  "region": "Chișinău",
  "city": "Chișinău",
  "street": "Str. Decebal",
  "buildingNumber": "23/1",
  "postalCode": "MD-2038"
}
```

---

## 📁 Структура проекта

```
.
├── pom.xml                 # зависимости Maven
├── mvnw*                   # Maven Wrapper (Win/Linux)
├── src
│   ├── main
│   │   ├── java/md/bumbac/api
│   │   │   ├── BumbacApplication.java
│   │   │   ├── config/           # SecurityConfig
│   │   │   ├── controller/       # REST-эндпойнты
│   │   │   ├── dto/              # запрос/ответ
│   │   │   ├── model/            # JPA-сущности
│   │   │   ├── repository/       # Spring Data
│   │   │   ├── security/         # JWT-фильтр
│   │   │   ├── service/          # бизнес-логика
│   │   │   └── util/             # TranslationUtil
│   │   └── resources
│   │       ├── application.yml
│   │       ├── application-mysql.yml
│   │       ├── application-mail.yml
│   │       ├── i18n/
│   │       ├── static/mock/
│   │       └── templates/
│   └── test/java/md/bumbac/api
└── .mvn/wrapper
```

---

## 🧪 Тесты

```bash
./mvnw test      # JUnit 5
```

---

## 🐳 Запуск в Docker

```bash
docker build -t bumbac-backend .
docker run --env-file .env -p 8080:8080 bumbac-backend
```

`healthcheck` смотрит `/actuator/health`.

---

## 📬 Контакты

Проект для **[Bumbac.md](https://bumbac.md)**  
Вопросы → issues / pull-requests / email.
