#!/bin/bash
set -e
echo "🎭 Запуск STAGING..."

# Обновляем develop ветки с обработкой изменений
cd ~/projects/staging/Bumbac-staging
git stash 2>/dev/null || true
git fetch origin
git checkout develop
git pull --rebase origin develop
git stash pop 2>/dev/null || true

cd ~/projects/staging/YearnBumbacFront-staging
git stash 2>/dev/null || true
git fetch origin
git checkout develop
git pull --rebase origin develop
git stash pop 2>/dev/null || true

# Останавливаем старые screen'ы
screen -X -S backend-staging quit 2>/dev/null || true
screen -X -S frontend-staging quit 2>/dev/null || true

# Backend (с проверкой .env.staging)
echo "🔧 Backend..."
cd ~/projects/staging/Bumbac-staging/backend
if [ -f .env.staging ]; then
    screen -dmS backend-staging bash -c 'cd ~/projects/staging/Bumbac-staging/backend && set -a && . .env.staging && set +a && mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=staging'
else
    screen -dmS backend-staging bash -c 'cd ~/projects/staging/Bumbac-staging/backend && mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=staging'
fi

# Ждём старт Spring
sleep 8

# Frontend (Nuxt dev)
echo "🎨 Frontend..."
cd ~/projects/staging/YearnBumbacFront-staging
if [ -f .env.staging ]; then
    screen -dmS frontend-staging bash -c 'cd ~/projects/staging/YearnBumbacFront-staging && source .env.staging && npm i --silent && npm run dev -- --host 0.0.0.0 --port 3002'
else
    screen -dmS frontend-staging bash -c 'cd ~/projects/staging/YearnBumbacFront-staging && npm i --silent && npm run dev -- --host 0.0.0.0 --port 3002'
fi

echo "✅ STAGING поднят."
bash "$(dirname "$0")/check-staging.sh"
