<!--
---
id: BUG-001
title: Данные профиля не обновляются после PUT /user/profile
priority: High
severity: Major
status: Open
environment: Google v.139.0.7258.128, Windows 10 Pro
author: liu-kn
---
-->

## 🐞 BUG-001 / Данные профиля не обновляются после PUT /user/profile
### 📅 Date: 2025-08-20
### 💻 Environment: Google v.139.0.7258.128, Windows 10 Pro
### 🚦 Priority: High
### ⚠️ Severity: Major
### 📌 Status: Open

### 🔧 Preconditions / Предусловия
Пользователь должен быть зарегистрирован

### 🔄 Steps to Reproduce / Шаги воспроизведения
1. 1. Выполнить POST /auth/login
2. 2. Отправить PUT /user/profile с телом: { "firstName": "Liubov", "lastName": QALiu", "phone": "+12345678910" }
3. 3. Выполнить GET /user/me

### 💭 Expected Result / Ожидаемый результат
1. 1. Код 200 ok, получен accessToken
2. 2.Получен ответ 200 OK с телом ответа profile updated
3. 3. Код 200 ОК, в теле ответа возвращаются обновленные данные: { "firstName": "Liubov", "lastName": QALiu", "phone": "+12345678910" }

### 🚨 Actual Result / Фактический результат
1. 1. Пользователь залогинен, accessToken получен
2. 2. Получен ответ 200 OK с телом ответа profile updated
3. 3. Код 200, GET /user/me возвращает: {    "id": 43,     "email": "email_2@bumbac.com",     "roles": [         "USER"      ],     "firstName": null,     "lastName": null, "phone": null  }

### 📎 Attachments / Вложения
- [коллекция в Postman](https://docs.google.com/presentation/d/1c3UhpnYVpC_a0wRUSP1KpRHUdM_6nyBfFhyetuUMyWk/edit?usp=sharing)

### 🛠️ Suggested Solution / Предлагаемое решение
1.Проверить сохранение данных на сервере после PUT, 2. Убедиться, что GET /user/me возвращает актуальные данные из БД, 3. Проверить, нет ли фильтрации/обрезки при сохранении

### 🧩 Additional Information / Дополнительная информация
Нет

### ✍️ Reported by: liu-kn
