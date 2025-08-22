@echo off
echo 🚀 Запуск локальной среды разработки Bumbac
echo.

REM Проверка Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker не запущен или не установлен
    echo Запустите Docker Desktop и попробуйте снова
    pause
    exit /b 1
)

echo 📊 Запуск базы данных и сервисов...
docker-compose -f docker-compose.local.yml up -d

echo ⏳ Ожидание готовности MySQL...
timeout /t 15 /nobreak

echo ☕ Запуск Backend (Spring Boot)...
cd backend
start "Backend" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=local"

echo ⏳ Ожидание запуска Backend...
timeout /t 20 /nobreak

echo 🌐 Запуск Frontend (Nuxt.js)...
cd ..\..\YearnBumbacFront
start "Frontend" cmd /k "npm run dev"

echo.
echo ✅ Локальная среда запускается!
echo.
echo 🌐 Frontend: http://localhost:3000
echo ☕ Backend:  http://localhost:8080
echo 📊 Adminer:  http://localhost:8082
echo 📧 MailHog:  http://localhost:8026
echo.
echo 📝 Для остановки запустите: stop-local.bat
echo.
pause