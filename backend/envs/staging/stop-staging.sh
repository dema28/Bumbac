#!/usr/bin/env bash
set -euo pipefail
echo "🛑 Остановка STAGING Backend..."

# Остановка только backend процесса
echo "📱 Остановка backend..."
screen -X -S backend-staging quit 2>/dev/null && echo "✅ Backend остановлен" || echo "ℹ️ Backend не был запущен"

# Проверим что порт освободился
sleep 2
if lsof -ti:8082 >/dev/null 2>&1; then
    echo "⚠️ Порт 8082 все еще занят, принудительное завершение..."
    lsof -ti:8082 | xargs kill -9 2>/dev/null || true
fi

echo "✅ STAGING Backend полностью остановлен"
