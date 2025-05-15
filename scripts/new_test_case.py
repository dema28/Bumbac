import os
import re
from datetime import date

def sanitize_filename(title):
    slug = re.sub(r"[^\w]+", "_", title.strip()).strip("_")
    return slug

def ask_steps(prompt):
    steps_input = input(f"{prompt} (через ;): ").strip()
    if ";" in steps_input:
        steps = steps_input.split(";")
    else:
        steps = [steps_input]
    return "\n".join([f"{i+1}. {step.strip()}" for i, step in enumerate(steps) if step.strip()])

def ask_result_block(name):
    mode = input(f"{name}: Хотите ввести как список шагов? (y/n): ").strip().lower()
    if mode == "y":
        return ask_steps(f"{name} шаги")
    else:
        return input(f"{name}: Введите одним абзацем: ").strip()

def ask_links(prompt):
    print(f"{prompt} — Введите одну ссылку на строку (пустая строка — завершить):")
    links = []
    while True:
        link = input("🔗 Введите ссылку: ").strip()
        if not link:
            break
        name = input("📎 Описание вложения: ").strip()
        links.append(f"[{name}]({link})")
    return "\n".join(links)

def main():
    print("🧪 Test Case Generator / Генератор тест-кейса (FULL MODE)")
    tc_id = input("🔢 Test Case ID (e.g. TC_001): ").strip()
    title = input("📝 Title / Название теста: ").strip()
    test_type = input("📂 Type (Functional, UI...): ").strip()
    priority = input("🚦 Priority (High/Medium/Low): ").strip()
    severity = input("⚠️ Severity (High/Medium/Low): ").strip()
    version = input("📦 Version (e.g. 1.0): ").strip()
    status = "Not Executed"
    environment = input("💻 Environment (browser, device, OS): ").strip()
    author = input("👤 Author name (кто создал тест): ").strip()
    description = input("📜 Description / Описание: ").strip()
    preconditions = input("🔧 Preconditions / Предусловия: ").strip()
    test_steps = ask_steps("🔄 Test Steps / Шаги теста")
    expected = ask_result_block("💭 Expected Result / Ожидаемый результат")
    actual = ask_result_block("🚨 Actual Result / Фактический результат")
    attachments = ask_links("📎 Attachments / Вложения (опционально)")

    slug = sanitize_filename(title)
    filename = f"{tc_id}_{slug}.md"
    folder = os.path.join(os.path.dirname(__file__), "..", "test_cases")
    folder = os.path.abspath(folder)
    os.makedirs(folder, exist_ok=True)
    path = os.path.join(folder, filename)

    content = "<!--\n---\n"
    content += f"id: {tc_id}\n"
    content += f"title: {title}\n"
    content += f"type: {test_type}\n"
    content += f"priority: {priority}\n"
    content += f"severity: {severity}\n"
    content += f"version: {version}\n"
    content += f"status: {status}\n"
    content += f"environment: {environment}\n"
    content += f"author: {author}\n"
    content += "---\n-->\n\n"

    content += f"## 🧪 {tc_id} / {title}\n"
    content += f"### 📅 Date: {date.today()}\n"
    content += f"### 💻 Environment: {environment}\n"
    content += f"### 🚦 Priority: {priority}\n"
    content += f"### ⚠️ Severity: {severity}\n"
    content += f"### 📦 Version: {version}\n"
    content += f"### 📌 Status: {status}\n\n"
    content += f"### 📜 Description / Описание\n{description}\n\n"
    content += f"### 🔧 Preconditions / Предусловия\n{preconditions}\n\n"
    content += f"### 🔄 Test Steps / Шаги тестирования\n{test_steps}\n\n"
    content += f"### 💭 Expected Result / Ожидаемый результат\n{expected}\n\n"
    content += f"### 🚨 Actual Result / Фактический результат\n{actual}\n\n"

    content += "### 📎 Attachments / Вложения\n"
    if attachments:
        for line in attachments.strip().splitlines():
            content += f"- {line.strip()}\n"
    else:
        content += "- Нет\n"
    content += "\n"

    #content += "### 🔗 Related Bugs / Features / Связанные баги и фичи\n- BUG-XXX\n- FEATURE-XXX\n\n"
    content += f"### 📋 Test Run Info / Инфо о прогонах\nLast updated: {date.today()}\nExecuted by: QA Engineer {author}\n"


    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"✅ Тест-кейс создан: {path}")

if __name__ == "__main__":
    main()
