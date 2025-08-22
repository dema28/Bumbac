@echo off
echo 🔍 Проверка локальной среды
echo.

REM Backend check
curl -f http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Backend работает
) else (
    echo ❌ Backend недоступен
)

REM Frontend check  
curl -f http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Frontend работает
) else (
    echo ❌ Frontend недоступен
)

REM Database check
docker exec bumbac-mysql-local mysqladmin ping -h localhost >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ MySQL работает
) else (
    echo ❌ MySQL недоступен
)

echo.
pause