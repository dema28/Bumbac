# 🚀 Локальный запуск Bumbac Backend

Подробное руководство по настройке и запуску проекта на локальной машине.

---

## 📋 Предварительные требования

### Обязательные компоненты

- **Java 17** или выше
  ```bash
  java -version
  # Должно показать: openjdk version "17.x.x"
  ```

- **Maven 3.8** или выше
  ```bash
  mvn -version
  # Должно показать: Apache Maven 3.8.x
  ```

- **MySQL 8.0** или выше
  ```bash
  mysql --version
  # Должно показать: mysql Ver 8.0.x
  ```

- **Git**
  ```bash
  git --version
  # Должно показать: git version 2.x.x
  ```

### Рекомендуемые инструменты

- **IDE**: IntelliJ IDEA, Eclipse, VS Code
- **Database Client**: MySQL Workbench, DBeaver
- **API Testing**: Postman, Insomnia
- **Docker** (опционально, для альтернативного запуска)

---

## 🔧 Установка и настройка

### 1. Клонирование репозитория

```bash
# Клонируем репозиторий
git clone https://github.com/your-org/bumbac-backend.git

# Переходим в директорию проекта
cd bumbac-backend

# Проверяем структуру
ls -la
```

### 2. Настройка базы данных

#### Создание базы данных

```sql
-- Подключаемся к MySQL
mysql -u root -p

-- Создаем базу данных
CREATE DATABASE yarn_store 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Создаем пользователя
CREATE USER 'bumbac_user'@'localhost' 
IDENTIFIED BY 'your_secure_password';

-- Даем права
GRANT ALL PRIVILEGES ON yarn_store.* 
TO 'bumbac_user'@'localhost';

-- Применяем изменения
FLUSH PRIVILEGES;

-- Проверяем
SHOW DATABASES;
USE yarn_store;
SHOW TABLES;
```

#### Альтернативный способ через командную строку

```bash
# Создание БД через командную строку
mysql -u root -p -e "
CREATE DATABASE yarn_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'bumbac_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON yarn_store.* TO 'bumbac_user'@'localhost';
FLUSH PRIVILEGES;
"
```

### 3. Настройка переменных окружения

#### Создание .env файла

```bash
# Копируем пример конфигурации
cp .env.example .env

# Открываем для редактирования
nano .env  # или используйте любой текстовый редактор
```

#### Содержимое .env файла

```env
# База данных
DB_HOST=localhost
DB_PORT=3306
DB_NAME=yarn_store
DB_USER=bumbac_user
DB_PASSWORD=your_secure_password

# JWT токены
JWT_SECRET=your_super_secret_jwt_key_at_least_32_characters_long
JWT_EXPIRATION=86400000

# Email (SMTP)
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password

# Rate Limiting
RATE_LIMIT_REQUESTS_PER_MINUTE=3
RATE_LIMIT_BURST_CAPACITY=5
RATE_LIMIT_LOGIN_ATTEMPTS_PER_MINUTE=5
RATE_LIMIT_LOGIN_BURST_CAPACITY=8

# Медиафайлы
MEDIA_UPLOAD_DIR=uploads/
MEDIA_BASE_URL=http://localhost:8080/media/

# Логирование
LOG_LEVEL=INFO
LOG_FILE=logs/bumbac-app.log
```

### 4. Проверка конфигурации

#### Проверка application.yml

Убедитесь, что в `src/main/resources/application.yml` правильно настроены:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:yarn_store}
    username: ${DB_USER:denis}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### Проверка зависимостей

```bash
# Проверяем, что все зависимости загружены
mvn dependency:resolve

# Проверяем структуру проекта
mvn validate
```

---

## 🚀 Запуск приложения

### Способ 1: Через Maven (рекомендуется)

```bash
# Очистка и сборка
mvn clean compile

# Запуск приложения
mvn spring-boot:run
```

### Способ 2: Через JAR файл

```bash
# Сборка JAR
mvn clean package

# Запуск JAR
java -jar target/bumbac-backend-1.0.0.jar
```

### Способ 3: Через IDE

1. Откройте проект в IDE
2. Найдите класс `BumbacApplication`
3. Запустите метод `main()`

### Способ 4: Через Docker (опционально)

```bash
# Сборка Docker образа
docker build -t bumbac-backend .

# Запуск контейнера
docker run -p 8080:8080 --env-file .env bumbac-backend
```

---

## ✅ Проверка работоспособности

### 1. Проверка запуска

После запуска в логах должно появиться:

```
Started BumbacApplication in X.XXX seconds (process running for X.XXX)
```

### 2. Проверка endpoints

#### Health Check
```bash
curl http://localhost:8080/actuator/health
```

Ожидаемый ответ:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

#### Swagger UI
Откройте в браузере: http://localhost:8080/swagger-ui.html

#### API Documentation
```bash
curl http://localhost:8080/v3/api-docs
```

### 3. Тестирование API

#### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+37360123456"
  }'
```

#### Вход в систему
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!"
  }'
```

---

## 🐛 Устранение неполадок

### Проблемы с базой данных

#### Ошибка подключения к MySQL
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Решение:**
1. Проверьте, что MySQL запущен:
   ```bash
   sudo systemctl status mysql
   # или
   brew services list | grep mysql
   ```

2. Проверьте настройки подключения в `.env`
3. Убедитесь, что пользователь имеет права доступа

#### Ошибка создания таблиц
```
Table 'yarn_store.users' doesn't exist
```

**Решение:**
1. Проверьте, что база данных создана
2. Проверьте права пользователя
3. Перезапустите приложение

### Проблемы с JWT

#### Ошибка JWT_SECRET
```
JWT_SECRET is not configured
```

**Решение:**
1. Добавьте `JWT_SECRET` в `.env`
2. Убедитесь, что ключ достаточно длинный (минимум 32 символа)

### Проблемы с портами

#### Порт 8080 занят
```
Web server failed to start. Port 8080 was already in use.
```

**Решение:**
1. Найдите процесс, использующий порт:
   ```bash
   lsof -i :8080
   ```

2. Остановите процесс или измените порт в `application.yml`:
   ```yaml
   server:
     port: 8081
   ```

### Проблемы с памятью

#### Недостаточно памяти
```
java.lang.OutOfMemoryError: Java heap space
```

**Решение:**
1. Увеличьте heap size:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2g"
   ```

2. Или добавьте в `application.yml`:
   ```yaml
   spring:
     jvm:
       args: "-Xmx2g"
   ```

---

## 🔧 Дополнительная настройка

### Настройка логирования

#### Изменение уровня логирования
В `application.yml`:
```yaml
logging:
  level:
    com.bumbac: DEBUG
    org.springframework.security: DEBUG
```

#### Настройка файла логов
```yaml
logging:
  file:
    name: logs/bumbac-app.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### Настройка кэширования

#### Включение Redis (опционально)
```yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

### Настройка мониторинга

#### Actuator endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: [health, info, prometheus, metrics]
```

---

## 📊 Мониторинг и отладка

### Логи приложения

#### Просмотр логов в реальном времени
```bash
tail -f logs/bumbac-app.log
```

#### Поиск ошибок
```bash
grep "ERROR" logs/bumbac-app.log
```

### Метрики приложения

#### Prometheus метрики
```bash
curl http://localhost:8080/actuator/prometheus
```

#### Health check
```bash
curl http://localhost:8080/actuator/health
```

### Отладка в IDE

#### Настройка отладки в IntelliJ IDEA
1. Создайте Run Configuration
2. Выберите "Spring Boot"
3. Укажите Main class: `com.bumbac.BumbacApplication`
4. Добавьте VM options: `-Dspring.profiles.active=dev`

---

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
mvn test

# Конкретный модуль
mvn test -Dtest=AuthModuleTest

# С покрытием кода
mvn jacoco:report
```

### Интеграционные тесты

```bash
# Запуск с профилем test
mvn spring-boot:run -Dspring.profiles.active=test
```

---

## 🔄 Обновление проекта

### Получение обновлений

```bash
# Получаем изменения
git pull origin main

# Обновляем зависимости
mvn clean install

# Перезапускаем приложение
mvn spring-boot:run
```

### Миграции базы данных

```bash
# Проверяем статус миграций
mvn flyway:info

# Применяем миграции
mvn flyway:migrate
```

---

## 📚 Полезные команды

### Maven команды
```bash
# Очистка и сборка
mvn clean compile

# Запуск тестов
mvn test

# Сборка JAR
mvn clean package

# Запуск приложения
mvn spring-boot:run

# Просмотр зависимостей
mvn dependency:tree
```

### Git команды
```bash
# Проверка статуса
git status

# Просмотр изменений
git diff

# Создание ветки
git checkout -b feature/new-feature

# Коммит изменений
git add .
git commit -m "feat: add new feature"
```

### Системные команды
```bash
# Проверка Java
java -version

# Проверка Maven
mvn -version

# Проверка MySQL
mysql --version

# Проверка портов
netstat -tulpn | grep :8080
```

---

## 📞 Поддержка

### Полезные ссылки

- **Документация Spring Boot**: https://spring.io/projects/spring-boot
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **Swagger Documentation**: https://swagger.io/docs/
- **JWT Documentation**: https://jwt.io/

### Сообщение об ошибках

При возникновении проблем:

1. Проверьте логи приложения
2. Убедитесь, что все требования выполнены
3. Создайте issue в GitHub с подробным описанием
4. Приложите логи и скриншоты ошибок

---

## 🎉 Поздравляем!

Если вы дошли до этого места, значит приложение успешно запущено и готово к работе! 

Теперь вы можете:
- Тестировать API через Swagger UI
- Разрабатывать новые функции
- Изучать код и архитектуру
- Вносить свой вклад в проект

Удачной разработки! 🚀
