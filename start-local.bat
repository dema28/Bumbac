@echo off
setlocal EnableExtensions EnableDelayedExpansion

REM ==== Resolve paths ====
set "SCRIPT_DIR=%~dp0"
pushd "%SCRIPT_DIR%\.."
set "ROOT=%CD%"
echo [INFO] Project root: "%ROOT%"

REM Prefer .env.local if present (used for docker compose only)
set "ENV_FILE=%ROOT%\.env.local"
if not exist "%ENV_FILE%" (
  set "ENV_FILE=%ROOT%\.env"
)
if exist "%ENV_FILE%" (
  echo [INFO] Using env file: "%ENV_FILE%"
) else (
  echo [WARN] No .env.local or .env found in %ROOT%
)

REM ==== Verify Docker ====
docker --version >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Docker Desktop is not installed or not running.
  echo         Please start Docker Desktop and try again.
  popd
  exit /b 1
)

REM ==== Pick compose file ====
set "COMPOSE_FILE=%ROOT%\docker-compose.local.yml"
if not exist "%COMPOSE_FILE%" (
  set "COMPOSE_FILE=%ROOT%\docker-compose.yml"
)
if exist "%COMPOSE_FILE%" (
  echo [INFO] Using compose file: "%COMPOSE_FILE%"
) else (
  echo [ERROR] docker-compose.yml not found in %ROOT%
  popd
  exit /b 1
)

REM ==== Start helper services ====
echo [INFO] Starting local services (MySQL/Mailhog/Adminer)...
if exist "%ENV_FILE%" (
  docker compose --env-file "%ENV_FILE%" -f "%COMPOSE_FILE%" up -d
) else (
  docker compose -f "%COMPOSE_FILE%" up -d
)
if errorlevel 1 (
  echo [ERROR] Failed to start docker services.
  popd
  exit /b 1
)

REM ==== Optional: wait for MySQL (simple loop) ====
echo [INFO] Waiting for MySQL to accept connections...
for /l %%i in (1,1,20) do (
  for /f "usebackq delims=" %%h in (`docker ps --format "{{.Names}}"`) do (
    if /i "%%~h"=="bumbac-mysql-local" (
      for /f "usebackq delims=" %%s in (`docker inspect -f "{{.State.Health.Status}}" bumbac-mysql-local 2^>nul`) do (
        if /i "%%~s"=="healthy" (
          echo [INFO] MySQL is healthy.
          goto mysql_ready
        )
      )
    )
  )
  ping -n 2 127.0.0.1 >nul
)
:mysql_ready

REM ==== Start backend (new window) ====
echo [INFO] Starting Spring Boot (profile=local)...
set "MVN_EXE=mvnw"
if not exist "%ROOT%\mvnw.cmd" (
  set "MVN_EXE=mvn"
)
start "Bumbac Backend (local)" cmd /k "cd /d "%ROOT%" && %MVN_EXE% -q spring-boot:run -Dspring-boot.run.profiles=local"

REM ==== Start frontend (Nuxt) ====
REM 1) If FRONTEND_DIR is set in system/user env, use it.
REM 2) Else try user-provided default path.
REM 3) Else try relative folders two and one levels up.
if not defined FRONTEND_DIR set "FRONTEND_DIR=C:\Users\user\YearnBumbacFront"
if not exist "%FRONTEND_DIR%\package.json" (
  if exist "%ROOT%\..\..\YearnBumbacFront\package.json" set "FRONTEND_DIR=%ROOT%\..\..\YearnBumbacFront"
)
if not exist "%FRONTEND_DIR%\package.json" (
  if exist "%ROOT%\..\YearnBumbacFront\package.json" set "FRONTEND_DIR=%ROOT%\..\YearnBumbacFront"
)
if not exist "%FRONTEND_DIR%\package.json" (
  if exist "%ROOT%\..\frontend\package.json" set "FRONTEND_DIR=%ROOT%\..\frontend"
)

if exist "%FRONTEND_DIR%\package.json" (
  echo [INFO] Frontend dir: "%FRONTEND_DIR%"
  where npm >nul 2>&1
  if errorlevel 1 (
    echo [WARN] Node.js/NPM not found in PATH. Skipping frontend start.
  ) else (
    echo [INFO] Starting Nuxt dev server...
    start "Frontend (Nuxt)" cmd /k "cd /d "%FRONTEND_DIR%" && npm install --no-fund --no-audit && npm run dev"
  )
) else (
  echo [WARN] Frontend project not found. Set FRONTEND_DIR to your Nuxt folder.
)

echo.
echo [OK] Local environment is up.
echo      Adminer:   http://localhost:8082
echo      Mailhog:   http://localhost:8026
echo      Backend:   http://localhost:8080/swagger-ui/index.html
echo      Frontend:  http://localhost:3000  (Nuxt default)
popd
exit /b 0
