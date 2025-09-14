#!/bin/bash
set -e
echo "🎭 Запуск STAGING..."

# Обновляем develop ветки
cd ~/projects/staging/Bumbac-staging
git stash --quiet 2>/dev/null || true
git fetch origin
git checkout develop
git pull --rebase origin develop
git stash pop --quiet 2>/dev/null || true

cd ~/projects/staging/YearnBumbacFront-staging
git stash --quiet 2>/dev/null || true
git fetch origin
git checkout develop
git pull --rebase origin develop
git stash pop --quiet 2>/dev/null || true

# Останавливаем старые screen-сессии
screen -X -S backend-staging quit 2>/dev/null || true
screen -X -S frontend-staging quit 2>/dev/null || true

# Backend
echo "🔧 Backend..."
cd ~/projects/staging/Bumbac-staging/backend
if [ -f .env.staging ]; then
    screen -dmS backend-staging bash -c '
        cd ~/projects/staging/Bumbac-staging/backend
        set -a && . .env.staging && set +a
        mkdir -p logs
        mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=staging | tee logs/staging_backend.log
    '
else
    screen -dmS backend-staging bash -c '
        cd ~/projects/staging/Bumbac-staging/backend
        mkdir -p logs
        mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=staging | tee logs/staging_backend.log
    '
fi

# Ждём старт Spring
sleep 20

# Frontend
echo "🎨 Frontend..."
cd ~/projects/staging/YearnBumbacFront-staging
if [ -f .env.staging ]; then
    screen -dmS frontend-staging bash -c '
        cd ~/projects/staging/YearnBumbacFront-staging
        source .env.staging
        if [ ! -d node_modules ]; then
            npm ci --silent
        fi
        npm run dev -- --host 0.0.0.0 --port 3002 | tee staging_frontend.log
    '
else
    screen -dmS frontend-staging bash -c '
        cd ~/projects/staging/YearnBumbacFront-staging
        if [ ! -d node_modules ]; then
            npm ci --silent
        fi
        npm run dev -- --host 0.0.0.0 --port 3002 | tee staging_frontend.log
    '
fi

echo "✅ STAGING поднят."

echo ""
echo "ℹ️ Для проверки состояния выполните вручную:"
echo "   ./scripts/check-staging.sh"
echo "   ./scripts/system_diagnosis_staging.sh"
