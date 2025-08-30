#!/bin/bash
echo "🛑 Остановка STAGING..."

# Останавливаем screen сессии
screen -X -S backend-staging quit 2>/dev/null || true
screen -X -S frontend-staging quit 2>/dev/null || true

# Дополнительно убиваем процессы по портам (на всякий случай)
sudo lsof -ti:8082 | xargs kill -9 2>/dev/null || true
sudo lsof -ti:3002 | xargs kill -9 2>/dev/null || true

# Убиваем процессы по имени
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "npm run dev" 2>/dev/null || true

echo "✅ Остановлено."
