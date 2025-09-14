#!/bin/bash

# Полная диагностика STAGING системы Bumbac.md
# Автор: Denis + GPT

echo "🔍 ПОЛНАЯ ДИАГНОСТИКА STAGING BUMBAC.MD"
echo "========================================"
echo "Дата: $(date)"
echo "Пользователь: $(whoami)"
echo "Хост: $(hostname)"
echo ""

print_header() {
    echo ""
    echo "📊 $1"
    echo "----------------------------------------"
}

# 1. ОБЩАЯ ИНФОРМАЦИЯ
print_header "ИНФОРМАЦИЯ О СИСТЕМЕ"
echo "OS: $(grep PRETTY_NAME /etc/os-release | cut -d'\"' -f2)"
echo "Kernel: $(uname -r)"
echo "Uptime: $(uptime -p)"
echo "Load Average: $(uptime | awk -F'load average:' '{print $2}')"
echo "CPU: $(nproc) cores"
echo "Memory: $(free -h | awk '/Mem/ {print $2}')"
echo "Disk: $(df -h / | awk '$6=="/"{print $2 " total, " $3 " used, " $4 " free"}')"

# 2. SCREEN СЕССИИ
print_header "SCREEN СЕССИИ"
screen -ls 2>/dev/null
if screen -ls | grep -q "backend-staging"; then
    echo "✅ Backend STAGING screen активна"
else
    echo "❌ Backend STAGING screen НЕ найдена"
fi
if screen -ls | grep -q "frontend-staging"; then
    echo "✅ Frontend STAGING screen активна"
else
    echo "❌ Frontend STAGING screen НЕ найдена"
fi

# 3. СЕТЕВЫЕ ПОРТЫ
print_header "СЕТЕВЫЕ ПОРТЫ"
ss -ltnp | grep -E ":8082|:3002|:3306|:8081|:1025|:8025" || echo "❌ Нет слушателей на staging-портах"

# 4. ПРОЦЕССЫ
print_header "ПРОЦЕССЫ"

echo "Java (Backend STAGING):"
ps -u denis -o pid,pcpu,pmem,etime,cmd | grep java | grep -v grep | \
awk '{printf "PID:%s CPU:%s%% MEM:%s%% ETIME:%s CMD:%s...\n", $1, $2, $3, $4, substr($0, index($0,$5), 80)}' \
|| echo "❌ Java не найдена"

echo ""
echo "Node.js (Frontend STAGING):"
ps -u denis -o pid,pcpu,pmem,etime,cmd | grep node | grep -v grep | \
awk '{printf "PID:%s CPU:%s%% MEM:%s%% ETIME:%s CMD:%s...\n", $1, $2, $3, $4, substr($0, index($0,$5), 80)}' \
|| echo "❌ Node.js не найден"

# 5. DOCKER
print_header "DOCKER КОНТЕЙНЕРЫ"
if command -v docker >/dev/null 2>&1; then
    docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
else
    echo "❌ Docker не установлен"
fi

# 6. NGINX
print_header "NGINX"
if systemctl is-active --quiet nginx; then
    echo "✅ Nginx активен"
    sudo nginx -t 2>&1 | grep -q "syntax is ok"
    if [ $? -eq 0 ]; then
        echo "✅ Конфигурация корректна"
    else
        echo "❌ Ошибка конфигурации"
    fi
else
    echo "❌ Nginx не активен"
fi

# 7. MYSQL STAGING
print_header "БАЗА ДАННЫХ MYSQL (STAGING)"
if mysql -u denis -p'Himik28@good' -e "SELECT 1;" yarn_store_staging >/dev/null 2>&1; then
    echo "✅ Подключение к yarn_store_staging успешно"
    tables=$(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SHOW TABLES;" | tail -n +2 | wc -l)
    echo "📊 Таблиц: $tables"
    echo "👤 Пользователей: $(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SELECT COUNT(*) FROM users;" | tail -1)"
    echo "🧵 Товаров (yarns): $(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SELECT COUNT(*) FROM yarns;" | tail -1)"
    echo "🛒 Корзин: $(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SELECT COUNT(*) FROM cart_items;" | tail -1)"
    echo "📦 Заказов: $(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SELECT COUNT(*) FROM orders;" | tail -1)"
else
    echo "❌ Ошибка подключения к yarn_store_staging"
fi

# 8. API ЭНДПОИНТЫ
print_header "API ЭНДПОИНТЫ (STAGING)"
endpoints=(
    "http://localhost:8082/actuator/health:Backend Health Local"
    "http://localhost:3002:Frontend Local"
    "https://staging-qscfgrt657.duckdns.org/actuator/health:Backend Health Nginx"
    "https://staging-qscfgrt657.duckdns.org/v3/api-docs:API Docs"
    "https://staging-qscfgrt657.duckdns.org/swagger-ui/index.html:Swagger UI"
)
for ep in "${endpoints[@]}"; do
    url=$(echo "$ep" | cut -d':' -f1-2)
    name=$(echo "$ep" | cut -d':' -f3-)
    code=$(curl -s -o /dev/null -w "%{http_code}" "$url" --connect-timeout 5)
    if [ "$code" = "200" ]; then
        echo "✅ $name ($url) - OK"
    elif [ "$code" = "000" ]; then
        echo "❌ $name ($url) - Недоступен"
    else
        echo "⚠️ $name ($url) - HTTP $code"
    fi
done

# 9. ДИСК и ЛОГИ
print_header "ДИСКОВОЕ ПРОСТРАНСТВО И ЛОГИ"
df -h / | tail -1 | awk '{print "Корень: "$3" из "$2" ("$5")"}'
log_dirs=(
    "/home/denis/projects/staging/Bumbac-staging/backend/logs"
    "/var/log/nginx"
    "/var/log/mysql"
)
for dir in "${log_dirs[@]}"; do
    if [ -d "$dir" ]; then
        size=$(du -sh "$dir" | cut -f1)
        echo "📁 $dir: $size"
    else
        echo "❌ $dir нет"
    fi
done

# 10. СИСТЕМНЫЕ РЕСУРСЫ
print_header "СИСТЕМНЫЕ РЕСУРСЫ"
top -bn1 | grep "Cpu(s)" | awk '{printf "CPU: %.1f%% загружен\n", $2}'
free -h | awk '/Mem/ {printf "RAM: %s из %s использовано\n", $3, $2}'
echo "Top процессы по CPU:"
ps aux --sort=-%cpu | head -6 | tail -5 | awk '{printf "%-10s %s%% %s\n", $1, $3, $11}'
echo "Top процессы по памяти:"
ps aux --sort=-%mem | head -6 | tail -5 | awk '{printf "%-10s %s%% %s\n", $1, $4, $11}'

# 11. ПРОЕКТНЫЕ ПАПКИ
print_header "ФАЙЛОВАЯ СИСТЕМА ПРОЕКТА (STAGING)"
project_dirs=(
    "/home/denis/projects/staging/Bumbac-staging/backend"
    "/home/denis/projects/staging/YearnBumbacFront-staging"
)
for dir in "${project_dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo "✅ $dir ($(du -sh "$dir" | cut -f1))"
    else
        echo "❌ $dir нет"
    fi
done

# 12. РЕЗЮМЕ
print_header "РЕЗЮМЕ ДИАГНОСТИКИ (STAGING)"
issues=0
if ! screen -ls | grep -q "backend-staging"; then
    echo "⚠️ Backend screen неактивен"
    ((issues++))
fi
if ! screen -ls | grep -q "frontend-staging"; then
    echo "⚠️ Frontend screen неактивен"
    ((issues++))
fi
if ! ss -ltnp | grep -q ":8082"; then
    echo "⚠️ Backend API (8082) не слушает"
    ((issues++))
fi
if ! ss -ltnp | grep -q ":3002"; then
    echo "⚠️ Frontend (3002) не слушает"
    ((issues++))
fi

if [ $issues -eq 0 ]; then
    echo "✅ Все компоненты STAGING работают!"
    echo "🌐 Сайт: https://staging-qscfgrt657.duckdns.org/"
    echo "📚 Swagger: https://staging-qscfgrt657.duckdns.org/swagger-ui/index.html"
else
    echo "⚠️ Найдено проблем: $issues"
    echo "🔧 Проверьте: ./start-staging.sh"
fi

# 13. СРАВНЕНИЕ PROD vs STAGING (ВСЕ ТАБЛИЦЫ)
print_header "СРАВНЕНИЕ PROD vs STAGING"

if mysql -u denis -p'Himik28@good' -e "SELECT 1;" yarn_store >/dev/null 2>&1 && \
   mysql -u denis -p'Himik28@good' -e "SELECT 1;" yarn_store_staging >/dev/null 2>&1; then

    prod_tables=$(mysql -u denis -p'Himik28@good' yarn_store -e "SHOW TABLES;" | tail -n +2)
    staging_tables=$(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SHOW TABLES;" | tail -n +2)

    prod_count=$(echo "$prod_tables" | wc -l)
    staging_count=$(echo "$staging_tables" | wc -l)

    echo "📊 Таблиц в PROD: $prod_count"
    echo "📊 Таблиц в STAGING: $staging_count"

    diff_tables=$(comm -3 <(echo "$prod_tables" | sort) <(echo "$staging_tables" | sort))
    if [ -n "$diff_tables" ]; then
        echo "⚠️ Различия в списке таблиц:"
        echo "$diff_tables"
    else
        echo "✅ Набор таблиц совпадает"
    fi

    echo ""
    echo "📋 Сравнение количества строк в таблицах:"
    for table in $prod_tables; do
        prod_rows=$(mysql -u denis -p'Himik28@good' yarn_store -e "SELECT COUNT(*) FROM $table;" 2>/dev/null | tail -1)
        staging_rows=$(mysql -u denis -p'Himik28@good' yarn_store_staging -e "SELECT COUNT(*) FROM $table;" 2>/dev/null | tail -1)

        if [ -n "$prod_rows" ] && [ -n "$staging_rows" ]; then
            if [ "$prod_rows" -eq "$staging_rows" ]; then
                echo "✅ $table → совпадает ($prod_rows строк)"
            else
                echo "⚠️ $table → PROD: $prod_rows | STAGING: $staging_rows"
            fi
        else
            echo "❌ $table → отсутствует в одной из баз"
        fi
    done
else
    echo "❌ Нет доступа к prod или staging базе"
fi
