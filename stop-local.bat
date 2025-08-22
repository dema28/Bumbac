@echo off
echo 🛑 Остановка локальной среды разработки
echo.

echo Остановка Docker сервисов...
docker-compose -f docker-compose.local.yml down

echo Остановка Java процессов...
taskkill /f /im java.exe 2>nul

echo Остановка Node.js процессов...
taskkill /f /im node.exe 2>nul

echo ✅ Локальная среда остановлена
pause