# ⚙️ Автоматизация: генераторы и синхронизация

Этот раздел проекта содержит скрипты для генерации `.md` файлов и отправки их в GitHub Issues с автоматическим добавлением в Project Board.

---

## 📂 Структура `scripts/`

```plaintext
scripts/
├── new_bug_report.py               # Генерация баг-репорта
├── new_checklist.py                # Генерация чек-листа
├── new_test_case.py                # Генерация тест-кейса
├── create_bug_issue_and_card.py   # Отправка багов в Issues + Project
├── create_checklist_issue_and_card.py # Отправка чек-листов
└── create_test_case_issue_and_card.py # Отправка тест-кейсов
```

---

## 🧪 Генерация файлов (ручной запуск)

```bash
# Сначала перейди в корень проекта или в папку scripts:
cd scripts

# Генерация .md файлов (запрашивается ввод данных)
python new_bug_report.py
python new_checklist.py
python new_test_case.py
```

Файлы появятся в:
- `bug_reports/`
- `checklists/`
- `test_cases/`

---

## 🚀 Синхронизация с GitHub (только для core-участников)

```bash
# Создание Issue и карточки вручную (если не используешь CI)
python create_bug_issue_and_card.py  # ← только если ты в роли CI/PM/разработчика
python create_checklist_issue_and_card.py  # ← только если ты в роли CI/PM/разработчика
python create_test_case_issue_and_card.py  # ← только если ты в роли CI/PM/разработчика
```

---

## 🤖 Автоматизация через CI (работает автоматически после push)

При push новых `.md` файлов:
- GitHub Actions запускает `.yml` файлы из `.github/workflows/`
- Скрипты создают GitHub Issue и карточку на доске проекта

---

## 🔐 Настройка токена

Создай `.env` файл или добавь в GitHub Secrets:

```
GH_PROJECT_TOKEN=ghp_ваш_токен_сюда
```

Либо используй переменную окружения при запуске:

```bash
export GH_PROJECT_TOKEN=ghp_...
```

---

## 📌 Требования

- Python 3.9+
- Библиотеки:
  ```bash
  pip install requests pyyaml python-dotenv
  ```

---

## 👤 Автор шаблона

> Структура автоматизации: [@DenisNovicov](https://github.com/dema28)
