@echo off
title Bumbac Force Stop

echo ПРИНУДИТЕЛЬНАЯ остановка Bumbac...
echo ВНИМАНИЕ: Все данные могут быть потеряны!
echo.
set /p "CONFIRM=Продолжить принудительную остановку? (y/n): "
if /i not "%CONFIRM%"=="y" (
  echo Отменено пользователем
  pause
  exit /b
)

echo Принудительная остановка Docker контейнеров...
docker kill bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local 2>nul
docker rm -f bumbac-mysql-local bumbac-mailhog-local bumbac-phpmyadmin-local 2>nul

echo Принудительное завершение всех Java процессов...
taskkill /f /im java.exe 2>nul

echo Принудительное завершение всех Node.js процессов...
taskkill /f /im node.exe 2>nul

echo Принудительное завершение процессов на портах...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5173') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3307') do taskkill /f /pid %%a 2>nul

echo Очистка Docker volumes (ОПЦИОНАЛЬНО)...
set /p "CLEAN_VOLUMES=Удалить данные MySQL? (y/n): "
if /i "%CLEAN_VOLUMES%"=="y" (
  docker volume rm backend_mysql_data 2>nul
  echo MySQL данные удалены
)

echo Принудительная остановка завершена.
echo Система полностью очищена.
pause