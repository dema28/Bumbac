@echo off
title 🚀 Bumbac Local Launcher

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║         🚀 Запуск Bumbac LOCAL окружения           ║
echo ╚════════════════════════════════════════════════════╝
echo.

cd /d %~dp0

:: 0. Определяем docker compose
set "COMPOSE=docker compose"
%COMPOSE% version >nul 2>&1
if errorlevel 1 set "COMPOSE=docker-compose"

:: 1. Запуск Docker
echo [1/5] 🐳 Запуск Docker сервисов...
%COMPOSE% --env-file ..\..\.env.local -f ..\..\docker-compose.local.yml up -d
echo    Docker сервисы запущены.

:: 2. Ожидание MySQL (пока контейнер не станет healthy)
echo [2/5] ⏳ Ожидание запуска MySQL контейнера...
:wait_mysql
docker inspect -f "{{.State.Health.Status}}" bumbac-mysql-local 2>nul | findstr "healthy" >nul
if errorlevel 1 (
    echo    ⏳ MySQL ещё не готов, жду...
    timeout /t 3 /nobreak >nul
    goto wait_mysql
)
echo    ✅ MySQL готов.

:: 3. Проверка статуса контейнеров
echo [3/5] 🔎 Статус контейнеров:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | findstr bumbac-
echo.

:: 4. Запуск backend
echo [4/5] ☕ Запуск backend (Spring Boot, профиль LOCAL)...
start "Bumbac Backend" cmd /k "cd /d C:\Users\user\Bumbac\backend && mvn spring-boot:run -Dspring.profiles.active=local"


echo    Ожидание запуска backend (15 секунд)...
timeout /t 15 /nobreak >nul

:: 5. Запуск frontend
echo [5/5] 🌐 Запуск frontend (Nuxt/Node)...
if exist "C:\Users\user\YearnBumbacFront" (
    start "Bumbac Frontend" cmd /k "cd /d C:\Users\user\YearnBumbacFront && npm install && npm run dev -- --port 3000 --host"
) else (
    echo    ⚠️  Папка фронтенда не найдена!
)


timeout /t 3 /nobreak >nul

:: Открытие интерфейсов
echo 🌍 Открытие интерфейсов...
start "" http://127.0.0.1:8081
start "" http://127.0.0.1:8080/swagger-ui.html
start "" http://127.0.0.1:8025
start "" http://127.0.0.1:3000


echo.
echo ╔════════════════════════════════════════════════════╗
echo ║          ✅ Все сервисы успешно запущены           ║
echo ╚════════════════════════════════════════════════════╝
echo.
echo 📌 Доступные интерфейсы:
echo   Backend:    http://127.0.0.1:8080
echo   Frontend:   http://127.0.0.1:3000
echo   PhpMyAdmin: http://127.0.0.1:8081
echo   MailHog:    http://127.0.0.1:8025
echo   Swagger:    http://127.0.0.1:8080/swagger-ui.html
echo.
pause
