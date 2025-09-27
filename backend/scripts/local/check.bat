@echo off
setlocal enabledelayedexpansion
title 📊 Bumbac Local Environment Status Check

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║       📊 Проверка Bumbac LOCAL окружения            ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Переходим в корневую папку backend
cd /d "%~dp0\..\..\"


:: Загружаем переменные из .env.local
for /f "tokens=1,2 delims==" %%a in ('findstr /R "^DB_USERNAME=" .env.local') do set DB_USER=%%b
for /f "tokens=1,2 delims==" %%a in ('findstr /R "^DB_PASSWORD=" .env.local') do set DB_PASSWORD=%%b
for /f "tokens=1,2 delims==" %%a in ('findstr /R "^DB_NAME=" .env.local') do set DB_NAME=%%b

:: 0. Определяем docker compose
set "COMPOSE=docker compose"
%COMPOSE% version >nul 2>&1
if errorlevel 1 set "COMPOSE=docker-compose"

:: 1. Проверка файлов конфигурации
echo [1/6] 📂 Проверка наличия файлов конфигурации...
if exist ".env.local" (
    echo    .env.local найден
) else (
    echo    ❌ .env.local НЕ найден
)

if exist "docker-compose.local.yml" (
    echo    docker-compose.local.yml найден
) else (
    echo    ❌ docker-compose.local.yml НЕ найден
)

:: 2. Статус Docker контейнеров
echo.
echo [2/6] 🐳 Статус Docker контейнеров...
%COMPOSE% --env-file .env.local -f docker-compose.local.yml ps

:: 3. Проверка подключения к базе данных
echo.
echo [3/6] 🗄️  Проверка подключения к базе данных (MySQL)...
set table_count=0
set user_count=
docker exec bumbac-mysql-local mysql -u %DB_USER% -p"%DB_PASSWORD%" %DB_NAME% -e "SELECT 'MySQL OK';" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo    MySQL подключение успешно
    for /f "tokens=*" %%i in ('docker exec bumbac-mysql-local mysql -N -u %DB_USER% -p"%DB_PASSWORD%" -D %DB_NAME% -e "SHOW TABLES;" 2^>nul') do (
        set /a table_count+=1
    )
    for /f %%i in ('docker exec bumbac-mysql-local mysql -N -u %DB_USER% -p"%DB_PASSWORD%" -D %DB_NAME% -e "SELECT COUNT(*) FROM users;" 2^>nul') do set user_count=%%i

    if !table_count! gtr 0 (
        echo    Найдено таблиц в БД: !table_count!
        if defined user_count echo    Пользователей в БД: !user_count!
    ) else (
        echo    ⚠️  Таблицы в БД не найдены - возможно нужна миграция
    )
) else (
    echo    ❌ MySQL недоступен
)

:: 4. Проверка Backend API
echo.
echo [4/6] ☕ Проверка Backend API...
curl -s -o nul -w "Health Check: HTTP %%{http_code}\n" http://127.0.0.1:8080/actuator/health
curl -s -o nul -w "Swagger UI:   HTTP %%{http_code}\n" http://127.0.0.1:8080/swagger-ui/index.html
curl -s -o nul -w "API Info:     HTTP %%{http_code}\n" http://127.0.0.1:8080/actuator/info
curl -s -o nul -w "API Test:     HTTP %%{http_code}\n" http://127.0.0.1:8080/api/yarns

:: 5. Проверка Frontend
echo.
echo [5/6] 🌐 Проверка Frontend...
curl -s -o nul -w "Port 3000:    HTTP %%{http_code}\n" http://127.0.0.1:3000
curl -s -o nul -w "Port 5173:    HTTP %%{http_code}\n" http://127.0.0.1:5173

:: 6. Проверка вспомогательных сервисов
echo.
echo [6/6] 🛠️  Проверка вспомогательных сервисов...
curl -s -o nul -w "PhpMyAdmin:   HTTP %%{http_code}\n" http://127.0.0.1:8081
curl -s -o nul -w "MailHog Web:  HTTP %%{http_code}\n" http://127.0.0.1:8025

:: ================================
:: СВОДКА СТАТУСА
:: ================================
echo.
echo ╔════════════════════════════════════════════════════╗
echo ║                 📊 СВОДКА СТАТУСА                   ║
echo ╚════════════════════════════════════════════════════╝

REM Docker контейнеры
set docker_ok=0
for %%C in (bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local) do (
    docker ps --format "{{.Names}}" | findstr /i "%%C" >nul && set /a docker_ok+=1
)
if !docker_ok! equ 3 (
    echo Docker контейнеры: Все запущены
) else (
    echo ❌ Docker контейнеры: Не все запущены (запущено !docker_ok! из 3)
)

REM Backend
curl -s -o nul http://127.0.0.1:8080/actuator/health
if %ERRORLEVEL% equ 0 (
    echo Backend API: Работает
) else (
    echo ❌ Backend API: НЕ работает
)

REM Frontend
set frontend_ok=0
curl -s -o nul http://127.0.0.1:3000 && set /a frontend_ok+=1
curl -s -o nul http://127.0.0.1:5173 && set /a frontend_ok+=1

if !frontend_ok! gtr 0 (
    echo Frontend: Работает
) else (
    echo ❌ Frontend: НЕ работает
)

REM Таблицы и пользователи в БД
if !table_count! gtr 0 (
    echo Таблиц в базе: !table_count!
    if defined user_count echo Пользователей в базе: !user_count!
) else (
    echo ⚠️  Таблицы в базе не найдены
)

echo.
echo 📋 Полезные ссылки:
echo   Backend API:     http://127.0.0.1:8080
echo   Health Check:    http://127.0.0.1:8080/actuator/health
echo   Swagger UI:      http://127.0.0.1:8080/swagger-ui.html
echo   Frontend:        http://127.0.0.1:3000
echo   PhpMyAdmin:      http://127.0.0.1:8081
echo   MailHog:         http://127.0.0.1:8025
echo.
echo 📁 Логи для отладки:
echo   Backend:         logs\bumbac-local.log
echo   Docker:          %COMPOSE% --env-file .env.local -f docker-compose.local.yml logs
echo.

pause
