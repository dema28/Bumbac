# 🖥 Bumbac - LOCAL Scripts (Windows)

> **Окружение:** LOCAL (Windows 11)  
> **Назначение:** Управление локальным окружением (Backend + Frontend + Docker)  
> **Статус:** ✅ Полностью готово

---

## 📋 Основные сервисы

| Сервис        | Контейнер                | Порт  | URL                        | Описание                  |
|---------------|--------------------------|-------|----------------------------|---------------------------|
| **Backend**   | локальный процесс (Java) | 8080  | http://localhost:8080      | Spring Boot (API, Swagger) |
| **Frontend**  | локальный процесс (Node) | 3000  | http://localhost:3000      | Nuxt.js приложение        |
| **MySQL**     | bumbac-mysql-local       | 3307  | localhost:3307             | База `yarn_store_local`   |
| **PhpMyAdmin**| bumbac-phpmyadmin-local  | 8081  | http://localhost:8081      | Web UI для MySQL          |
| **MailHog**   | bumbac-mailhog-local     | 8025  | http://localhost:8025      | Email тестирование (SMTP: 1025) |

---

## 📂 Доступные скрипты

- **`start-local.bat`** — 🚀 запускает окружение:  
  поднимает Docker-сервисы (MySQL, PhpMyAdmin, MailHog), ждёт готовности MySQL, запускает Backend (Spring Boot, профиль `local`) и Frontend (Nuxt.js), открывает интерфейсы в браузере.

- **`stop-local.bat`** — 🛑 корректно останавливает окружение:  
  останавливает Docker-контейнеры, завершает процессы Java (Spring Boot) и Node.js (Frontend), проверяет освобождение портов.

- **`check-local.bat`** — 📊 проверяет текущее состояние:  
  наличие `.env.local` и `docker-compose.local.yml`, статус Docker-контейнеров, доступность MySQL и количество таблиц/пользователей, доступность Backend (health, swagger, api), Frontend (3000/5173), PhpMyAdmin и MailHog.

- **`force-stop-local.bat`** — 🛑 аварийная остановка:  
  жёстко останавливает/удаляет контейнеры MySQL, PhpMyAdmin, MailHog, завершает процессы Java и Node.js, освобождает занятые порты (8080, 3000, 5173, 3307, 8081, 8025, 1025). Использовать только если обычный `stop-local.bat` не помог.

---

## ▶️ Типичный сценарий работы

1. **Запуск окружения** — `start-local.bat`  
   После запуска доступны:
    - Backend → http://localhost:8080
    - Frontend → http://localhost:3000
    - PhpMyAdmin → http://localhost:8081
    - MailHog → http://localhost:8025

2. **Проверка состояния** — `check-local.bat`  
   Показывает доступность сервисов, статус контейнеров, количество таблиц и пользователей в базе.

3. **Остановка окружения** — `stop-local.bat`  
   Корректно завершает все процессы и контейнеры.

4. **Аварийная остановка** — `force-stop-local.bat`  
   Используется, если обычная остановка не сработала (например, контейнеры зависли).

---

## 🔄 Визуальная схема

### Логика скриптов
```text
 ┌────────────┐
 │ start.bat  │   🚀 Запуск окружения
 └─────┬──────┘
       │
       ▼
 ┌────────────┐
 │ check.bat  │   📊 Проверка состояния
 └─────┬──────┘
   ┌───┴───────────────┐
   │                   │
   ▼                   ▼
┌────────────┐     ┌───────────────┐
│ stop.bat   │ 🛑  │ force-stop.bat │ 🛑 (аварийно)
└────────────┘     └───────────────┘

---
```
## 🔄 Компоненты окружения

### Запускаемые сервисы
```text
 ┌──────────────────────────────────────────┐
 │              LOCAL Environment           │
 ├──────────────────────────────────────────┤
 │  Frontend (Nuxt.js)  → http://localhost:3000 │
 │  Backend (Spring)    → http://localhost:8080 │
 ├──────────────────────────────────────────┤
 │  MySQL (порт 3307, volume: mysql_local_data) │
 │  PhpMyAdmin          → http://localhost:8081 │
 │  MailHog Web UI      → http://localhost:8025 │
 │  MailHog SMTP        → порт 1025             │
 └──────────────────────────────────────────┘
