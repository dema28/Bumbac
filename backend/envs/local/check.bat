@echo off
title Bumbac Local Check
cd /d %~dp0

echo Проверка окружения Bumbac LOCAL...
echo ================================

:: Проверка Docker контейнеров
echo Контейнеры Docker:
docker ps --format "table {{.Names}}\t{{.Status}}" | findstr /i "bumbac-" || echo ❌ Контейнеры не найдены!

:: Тест подключения к базе данных
echo.
echo База данных MySQL:
docker exec bumbac-mysql-local mysql -u denis -p"Himik28@good" yarn_store_local -e "SELECT 'MySQL OK' as status;" 2>nul && echo ✅ MySQL подключение успешно || echo ❌ MySQL недоступен

:: Проверка backend
echo.
echo Backend (localhost:8080):
curl -s -o nul -w "  → Health: %%{http_code}\n" http://localhost:8080/actuator/health || echo ❌ Backend не доступен
curl -s -o nul -w "  → Swagger: %%{http_code}\n" http://localhost:8080/swagger-ui.html

:: Проверка frontend
echo.
echo Frontend:
curl -s -o nul -w "  → Port 3000: %%{http_code}\n" http://localhost:3000
curl -s -o nul -w "  → Port 5173: %%{http_code}\n" http://localhost:5173

:: Проверка вспомогательных сервисов
echo.
echo Вспомогательные сервисы:
curl -s -o nul -w "  → PhpMyAdmin: %%{http_code}\n" http://localhost:8093
curl -s -o nul -w "  → MailHog: %%{http_code}\n" http://localhost:8045

:: Проверка backend .env файла
echo.
echo Backend .env файл:
setlocal enabledelayedexpansion
set "BACKEND_ENV_PATH=C:\Users\user\Bumbac\backend\envs\local\.env"
if exist "%BACKEND_ENV_PATH%" (
  echo ✅ Найден файл: %BACKEND_ENV_PATH%
  for /f "usebackq delims=" %%A in ("%BACKEND_ENV_PATH%") do (
    set "line=%%A"
    echo !line! | findstr "=" > nul
    if !errorlevel! == 0 (
      for /f "tokens=1 delims==" %%B in ("!line!") do (
        echo    → %%B = [скрыто]
      )
    )
  )
) else (
  echo ❌ Backend .env файл не найден по пути %BACKEND_ENV_PATH%
)

:: Проверка frontend .env.local файла
echo.
echo Frontend .env.local файл:
set "FRONTEND_ENV_PATH=C:\Users\user\YearnBumbacFront\.env.local"
if exist "%FRONTEND_ENV_PATH%" (
  echo ✅ Найден файл: %FRONTEND_ENV_PATH%
  for /f "usebackq delims=" %%A in ("%FRONTEND_ENV_PATH%") do (
    set "line=%%A"
    echo !line! | findstr "=" > nul
    if !errorlevel! == 0 (
      for /f "tokens=1 delims==" %%B in ("!line!") do (
        echo    → %%B = [скрыто]
      )
    )
  )
) else (
  echo ❌ Frontend .env файл не найден по пути %FRONTEND_ENV_PATH%
)
endlocal

:: Проверка логов
echo.
echo Логи:
if exist "C:\Users\user\Bumbac\backend\logs\bumbac-local.log" (
  echo ✅ Backend лог найден
  for /f %%i in ('find /c /v "" ^< "C:\Users\user\Bumbac\backend\logs\bumbac-local.log"') do echo    → Строк в логе: %%i
) else (
  echo ❌ Backend лог не найден
)

:: Проверка Java процессов
echo.
echo Java процессы:
tasklist /fi "imagename eq java.exe" | findstr java.exe && echo ✅ Java процессы найдены || echo ❌ Java процессы не найдены

:: Финальная сводка
echo.
echo ================================
echo Статус системы:
docker ps --format "table {{.Names}}\t{{.Status}}" | findstr /i "bumbac-" >nul && (
  echo ✅ Docker контейнеры запущены
) || (
  echo ❌ Docker контейнеры НЕ запущены
)

curl -s -o nul http://localhost:8080/actuator/health && (
  echo ✅ Backend работает
) || (
  echo ❌ Backend НЕ работает
)

:: Открытие интерфейсов (только если пользователь хочет)
echo.
set /p "OPEN_BROWSER=Открыть браузер с интерфейсами? (y/n): "
if /i "%OPEN_BROWSER%"=="y" (
  echo Открытие браузера...
  start "" http://localhost:8093
  start "" http://localhost:8045
  start "" http://localhost:8080/swagger-ui.html
  start "" http://localhost:3000 2>nul || start "" http://localhost:5173 2>nul
)

echo.
echo Проверка завершена.
pause