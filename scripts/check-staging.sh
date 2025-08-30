#!/bin/bash
echo "🔎 CHECK STAGING"
echo "— Screen sessions:"
screen -ls | grep -E "backend-staging|frontend-staging" || echo "no screen sessions"

echo "— Ports:"
ss -ltnp | grep -E ":8082|:3002" || echo "no listeners on 8082/3002"

echo "— Backend health via nginx:"
curl -s -o /dev/null -w "actuator/health: %{http_code}\n" https://staging-qscfgrt657.duckdns.org/actuator/health
echo "— API docs via nginx:"
curl -s -o /dev/null -w "v3/api-docs: %{http_code}\n" https://staging-qscfgrt657.duckdns.org/v3/api-docs
echo "— Swagger UI via nginx:"
curl -s -o /dev/null -w "swagger-ui/index.html: %{http_code}\n" https://staging-qscfgrt657.duckdns.org/swagger-ui/index.html
