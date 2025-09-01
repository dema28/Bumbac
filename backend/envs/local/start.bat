@echo off
title Bumbac Local Launcher

echo Запуск Bumbac LOCAL окружения...
cd /d %~dp0

echo Запуск docker-compose...
docker-compose up -d

echo Ожидание запуска MySQL контейнера...
timeout /t 10 /nobreak

echo Проверка статуса контейнеров...
docker ps --format "table {{.Names}}\t{{.Status}}" | findstr bumbac-

echo ВАЖНО: Запуск backend с профилем LOCAL...
start "Bumbac Backend" cmd /k "cd /d C:\Users\user\Bumbac\backend && mvn spring-boot:run -Dspring-boot.run.profiles=local"

echo Ожидание запуска backend (15 секунд)...
timeout /t 15 /nobreak

echo Запуск frontend...
start "Bumbac Frontend" cmd /k "cd /d C:\Users\user\YearnBumbacFront && npm run dev"

timeout /t 3 /nobreak

echo Открытие интерфейсов...
start "" http://localhost:8093
start "" http://localhost:8080/swagger-ui.html

echo.
echo Все сервисы запущены:
echo   Backend: http://localhost:8080
echo   Frontend: http://localhost:3000 или http://localhost:5173
echo   PhpMyAdmin: http://localhost:8093
echo   MailHog: http://localhost:8045
echo   Swagger: http://localhost:8080/swagger-ui.html
echo.
echo ВНИМАНИЕ: Убедитесь что backend запустился с профилем 'local'!
pause