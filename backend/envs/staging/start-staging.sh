#!/usr/bin/env bash
set -euo pipefail

echo "🎭 Запуск STAGING окружения..."

BACKEND_DIR="$HOME/projects/staging/Bumbac-staging/backend"
FRONTEND_DIR="$HOME/projects/staging/YearnBumbacFront-staging"
ENV_NEW="$HOME/projects/staging/Bumbac-staging/envs/staging/.env"
ENV_OLD="$BACKEND_DIR/.env.staging"

# 1) Обновляем код (develop)
echo "📥 Обновление кода..."
cd "$HOME/projects/staging/Bumbac-staging" && git fetch origin && git checkout develop && git pull --ff-only origin develop
cd "$HOME/projects/staging/YearnBumbacFront-staging" && git fetch origin && git checkout develop && git pull --ff-only origin develop

# 2) Гасим старые процессы
echo "🛑 Остановка старых процессов..."
screen -X -S backend-staging quit 2>/dev/null || true
screen -X -S frontend-staging quit 2>/dev/null || true

# 3) Готовим ENV backend
ENV_TO_USE=""
if [[ -f "$ENV_NEW" ]]; then
  ENV_TO_USE="$ENV_NEW"
  echo "🔐 Использую env: $ENV_TO_USE"
elif [[ -f "$ENV_OLD" ]]; then
  ENV_TO_USE="$ENV_OLD"
  echo "🔐 Внимание: новый env не найден, использую старый: $ENV_TO_USE"
else
  echo "❌ Не найден файл окружения: $ENV_NEW или $ENV_OLD"
  exit 1
fi

# 4) Frontend зависимости
echo "📦 Обновление зависимостей frontend..."
cd "$FRONTEND_DIR" && npm install

# 5) Запуск backend (Maven + profile=staging)
echo "🔧 Запуск Backend (profile=staging)..."
screen -dmS backend-staging bash -lc "
  cd '$BACKEND_DIR' && \
  set -a && source '$ENV_TO_USE' && set +a && \
  mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=\${SPRING_PROFILES_ACTIVE:-staging}
"

# 6) Ожидаем подъем backend
sleep 12

# 7) Запуск frontend (dev на 3002)
echo "🎨 Запуск Frontend (порт 3002)..."
screen -dmS frontend-staging bash -lc "
  cd '$FRONTEND_DIR' && \
  source .env.staging 2>/dev/null || true && \
  npm run dev -- --host 0.0.0.0 --port 3002
"

# 8) Быстрые проверки
echo "🔎 Проверка health backend:"
curl -s http://127.0.0.1:8082/actuator/health || true

echo "✅ STAGING запущен на https://staging-qscfgrt657.duckdns.org/"
echo "ℹ️  Логи: screen -r backend-staging  |  screen -r frontend-staging  (Ctrl+A, D — выйти)"
