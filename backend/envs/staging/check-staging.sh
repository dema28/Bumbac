#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
# shellcheck disable=SC1090
source "$SCRIPT_DIR/.env"

echo "🖥  screen:"
screen -ls | grep backend-staging || echo "нет screen-сессии backend-staging"

echo -e "\n🔌 Порты:"
ss -ltnp | egrep ":${SERVER_PORT}" || true

echo -e "\n❤️ Health (localhost):"
set +e
curl -sf "http://127.0.0.1:${SERVER_PORT}/actuator/health" && echo "" || echo "❌ недоступен"
set -e

if [[ -n "${STAGING_DOMAIN:-}" ]]; then
  echo -e "\n🌐 Health (https://${STAGING_DOMAIN}):"
  set +e
  curl -skf "https://${STAGING_DOMAIN}/actuator/health" && echo "" || echo "❌ недоступен"
  set -e
fi
