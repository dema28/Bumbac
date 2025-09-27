@echo off
title 🛑 Bumbac Local Stopper

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║           🛑 Остановка Bumbac LOCAL окружения      ║
echo ╚════════════════════════════════════════════════════╝
echo.

cd /d %~dp0

:: 1. Остановка Docker контейнеров
echo [1/4] 🐳 Остановка Docker сервисов...
docker-compose --env-file ..\..\.env.local -f ..\..\docker-compose.local.yml down
echo    Docker сервисы остановлены.

:: 2. Завершение Java (Spring Boot)
echo [2/4] ☕ Завершение Java процессов (Spring Boot)...
tasklist /fi "imagename eq java.exe" | findstr java.exe >nul && (
    taskkill /f /im java.exe >nul 2>&1
    echo    Java процессы завершены.
) || (
    echo    Java процессы не найдены.
)

:: 3. Завершение Node.js (Frontend)
echo [3/4] 🌐 Завершение Node.js процессов (Frontend)...
tasklist /fi "imagename eq node.exe" | findstr node.exe >nul && (
    taskkill /f /im node.exe >nul 2>&1
    echo    Node.js процессы завершены.
) || (
    echo    Node.js процессы не найдены.
)

:: 4. Проверка освобождения портов
echo [4/4] 🔍 Проверка портов (ожидание освобождения)...
timeout /t 10 /nobreak >nul

for %%P in (8080 3000 5173 3307 8081 8025 1025) do (
    netstat -ano | findstr :%%P >nul && (
        echo    ⚠️  Порт %%P все еще занят (возможно TIME_WAIT)
    ) || (
        echo    ✅ Порт %%P свободен
    )
)

:: Итог
echo.
echo ╔════════════════════════════════════════════════════╗
echo ║        ✅ Все процессы и контейнеры остановлены    ║
echo ╚════════════════════════════════════════════════════╝
echo.

echo 📋 Статус контейнеров после остановки:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | findstr bumbac-

echo.
pause
