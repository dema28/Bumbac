@echo off
title 🛑 Bumbac Local Force Stop

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║       🛑 Принудительная остановка окружения         ║
echo ╚════════════════════════════════════════════════════╝
echo.
echo ВНИМАНИЕ: Скрипт жёстко завершает процессы, но НЕ трогает данные MySQL.
echo Использовать ТОЛЬКО если обычный stop.bat не помогает.
echo.

set /p "CONFIRM=Продолжить принудительную остановку? (y/n): "
if /i not "%CONFIRM%"=="y" (
    echo Отменено пользователем
    pause
    exit /b
)

echo [1/4] Попытка мягкой остановки Docker контейнеров...
docker stop bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local 2>nul

echo [2/4] Принудительное завершение и удаление контейнеров...
docker kill bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local 2>nul
docker rm -f  bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local 2>nul

echo [3/4] Завершение Java и Node.js процессов...
taskkill /f /im java.exe  2>nul
taskkill /f /im node.exe  2>nul

echo [4/4] Освобождение занятых портов (8080, 3000, 5173, 3307, 8081, 8025, 1025)...
for %%P in (8080 3000 5173 3307 8081 8025 1025) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%%P') do taskkill /f /pid %%a 2>nul
)

echo.
echo ╔════════════════════════════════════════════════════╗
echo ║   Принудительная остановка успешно завершена       ║
echo ╚════════════════════════════════════════════════════╝
echo Все процессы сняты, данные MySQL сохранены.
echo.
pause
