# 🧶 Bumbac — Мультиязычный магазин пряжи

**Bumbac** — командный проект по созданию современного, адаптивного, мультиязычного сайта-магазина пряжи.  
Проект строится на открытых процессах: разработка, тестирование, автоматизация, CI/CD.

---

## 📂 Структура проекта

```plaintext
/
├── .github/                    # GitHub Workflows и шаблоны задач
│   └── workflows/             # CI для карточек (Issues + Project)
├── assets/                    # Изображения, баннеры, лого
├── backend/                   # Java (Spring Boot) API логика
├── frontend/                  # HTML + Tailwind шаблон
├── bug_reports/               # Баг-репорты в формате .md
├── checklists/                # Чек-листы в формате .md
├── test_cases/                # Тест-кейсы в формате .md
├── docs/                      # Общая документация
├── scripts/                   # Генераторы и sync-скрипты (для CI/разработчиков)
├── requirements.txt           # Зависимости проекта
├── .env.example               # Шаблон для переменной окружения с GitHub токеном
├── README_AUTOMATION.md       # Подробности по скриптам и генерации
└── README.md                  # Этот файл
```

---

## 🔄 Как работает автоматизация

- Все `.md` файлы (баги, тест-кейсы, чек-листы) автоматически обрабатываются GitHub Actions при `git push`
- GitHub создаёт:
  - соответствующий **Issue**
  - **карточку** в Project (beta)
  - размещает её в нужную колонку (`To Do`, `Bugs`, и т.д.)

✅ **Участникам проекта не нужно запускать никаких скриптов вручную**  
Просто: **генерируешь → коммитишь → пушишь** — и всё работает.

---

## 🧪 Как использовать

### Установка зависимостей:

```bash
pip install -r requirements.txt
```

### Генерация .md-файлов (ручной запуск):

```bash
python scripts/new_test_case.py
python scripts/new_bug_report.py
python scripts/new_checklist.py
```

- Файлы появятся в соответствующих папках:
  - `test_cases/`
  - `bug_reports/`
  - `checklists/`

- Сделай `git add`, `commit`, `push` — и CI создаст карточку на GitHub доске

---

## 🤖 Sync-скрипты (только для разработчиков и CI)

Скрипты `create_*.py` не обязательны для участников. Они используются:
- для ручной синхронизации
- тестирования без CI
- CI-ботами (через `.github/workflows/*.yml`)

```bash
python scripts/create_test_case_issue_and_card.py
python scripts/create_bug_issue_and_card.py
python scripts/create_checklist_issue_and_card.py
```

---

## 🔐 Переменная окружения (только для CI или локального sync)

Создай `.env` на основе `.env.example`:

```bash
GH_PROJECT_TOKEN=ghp_ваш_токен
```

Или задай через GitHub Secrets → `Settings → Secrets → Actions → GH_PROJECT_TOKEN`

---

## 👥 Команда

| Имя             | Роль                   |
|------------------|------------------------|
| Denis Novicov   | PM / QA / Content      |
| (Имя)           | Backend developer      |
| (Имя)           | Frontend developer     |
| (Имя)           | UI/UX designer         |
| (Имя)           | QA engineer            |

---

## 📄 Дополнительно

📘 Инструкция по работе с HTML-шаблоном:  
[frontend/README_frontend.md](frontend/README_frontend.md)

📘 Подробная документация по автоматизации:  
[README_AUTOMATION.md](scripts/README_AUTOMATION.md)
# Test Deploy Пт 15 авг 2025 18:58:46 CEST
