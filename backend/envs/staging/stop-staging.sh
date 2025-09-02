#!/usr/bin/env bash
set -euo pipefail
echo "🛑 Остановка STAGING окружения..."
screen -X -S backend-staging quit 2>/dev/null || true
screen -X -S frontend-staging quit 2>/dev/null || true
echo "✅ STAGING остановлен."
