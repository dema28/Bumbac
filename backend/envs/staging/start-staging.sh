#!/usr/bin/env bash
set -euo pipefail
echo "🎭 Запуск STAGING Backend..."

BACKEND_DIR="$HOME/projects/staging/Bumbac-staging/backend"
ENV_NEW="$HOME/projects/staging/Bumbac-staging/backend/envs/staging/.env"
ENV_OLD="$BACKEND_DIR/.env.staging"

# 1) Обновляем код (develop) - только backend
echo "📥 Обновление backend кода..."
cd "$HOME/projects/staging/Bumbac-staging" && git fetch origin && git checkout develop && git pull --ff-only origin develop

# 2) Гасим старый backend процесс
echo "🛑 Остановка старого backend..."
screen -X -S backend-staging quit 2>/dev/null || true

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

# 4) Запуск backend (Maven + profile=staging)
echo "🔧 Запуск Backend (profile=staging)..."
screen -dmS backend-staging bash -lc "
  cd '$BACKEND_DIR' && \
  set -a && source '$ENV_TO_USE' && set +a && \
  mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=\${SPRING_PROFILES_ACTIVE:-staging}
"

# 5) Ожидаем подъем backend
sleep 15

# 6) Быстрые проверки
echo "🔎 Проверка health backend:"
curl -s http://127.0.0.1:8082/actuator/health || true

echo ""
echo "✅ STAGING Backend запущен на https://staging-qscfgrt657.duckdns.org/"
echo "📋 Доступные сервисы:"
echo "   API: https://staging-qscfgrt657.duckdns.org/api"
echo "   Swagger: https://staging-qscfgrt657.duckdns.org/swagger-ui.html"
echo "   Health: https://staging-qscfgrt657.duckdns.org/actuator/health"
echo "ℹ️  Логи backend: screen -r backend-staging (Ctrl+A, D — выйти)"
