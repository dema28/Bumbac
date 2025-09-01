@echo off
title Bumbac Local Stopper

echo Остановка Bumbac LOCAL окружения...
cd /d %~dp0

echo Остановка Docker контейнеров...
docker-compose down

echo Завершение Java процессов (Spring Boot)...
tasklist /fi "imagename eq java.exe" | findstr java.exe >nul && (
  echo   Java процессы найдены, завершение...
  taskkill /f /im java.exe 2>nul
  echo   Java процессы завершены
) || (
  echo   Java процессы не найдены
)

echo Завершение Node.js процессов (Frontend)...
tasklist /fi "imagename eq node.exe" | findstr node.exe >nul && (
  echo   Node.js процессы найдены, завершение...
  taskkill /f /im node.exe 2>nul
  echo   Node.js процессы завершены
) || (
  echo   Node.js процессы не найдены
)

echo Проверка портов...
netstat -ano | findstr :8080 >nul && echo   Порт 8080 все еще занят || echo   Порт 8080 свободен
netstat -ano | findstr :3000 >nul && echo   Порт 3000 все еще занят || echo   Порт 3000 свободен
netstat -ano | findstr :5173 >nul && echo   Порт 5173 все еще занят || echo   Порт 5173 свободен
netstat -ano | findstr :3307 >nul && echo   Порт 3307 все еще занят || echo   Порт 3307 свободен

echo Проверка статуса Docker контейнеров...
docker ps -a --filter "name=bumbac-" --format "table {{.Names}}\t{{.Status}}"

echo.
echo Все процессы завершены.
echo Система готова к повторному запуску.
pause