import os
import re
from datetime import date

def sanitize_filename(title):
    return re.sub(r"[^\w]+", "_", title.strip()).strip("_")

def ask_check_items():
    print("Введите пункты чек-листа (через ;):")
    items_input = input("📝 Чек-пункты: ").strip()
    if ";" in items_input:
        items = items_input.split(";")
    else:
        items = [items_input]
    return "\n".join([f"- [{i+1}] {item.strip()}" for i, item in enumerate(items) if item.strip()])

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
    print("📋 Checklist Generator / Генератор чек-листа (FULL MODE)")
    cl_id = input("🆔 Checklist ID (например, CL_001): ").strip()
    title = input("📝 Название чек-листа: ").strip()
    section = input("📂 Раздел (UI/Install/Gameplay/...): ").strip()
    version = input("📦 Версия: ").strip()
    environment = input("💻 Окружение (эмулятор, устройство, ОС): ").strip()
    author = input("👤 Автор чек-листа: ").strip()
    checklist_items = ask_check_items()
    attachments = ask_links("📎 Вложения (опционально)")
    date_str = date.today().strftime('%Y-%m-%d')
    slug = sanitize_filename(title)

    filename = f"{cl_id}_{slug}.md"
    folder = os.path.join(os.path.dirname(__file__), "..", "checklists")
    folder = os.path.abspath(folder)
    os.makedirs(folder, exist_ok=True)
    path = os.path.join(folder, filename)

    content = "<!--\n---\n"
    content += f"id: {cl_id}\n"
    content += f"title: {title}\n"
    content += f"type: checklist\n"
    content += f"section: {section}\n"
    content += f"version: {version}\n"
    content += f"environment: {environment}\n"
    content += f"date: {date_str}\n"
    content += f"author: {author}\n"
    content += "---\n-->\n"

    content += f"## 📋 {cl_id} / {title}\n"
    content += f"### 📅 Date: {date_str}\n"
    content += f"### 💻 Environment: {environment}\n"
    content += f"### 📦 Version: {version}\n"
    content += f"### 📂 Section: {section}\n\n"

    content += "### ✅ Проверки / Checks\n\n"
    content += f"{checklist_items}\n\n"

    content += "### 📎 Вложения / Attachments\n"
    if attachments:
        for line in attachments.strip().splitlines():
            content += f"- {line.strip()}\n"
    else:
        content += "- Нет\n"

    content += f"\n### ℹ️ Последнее обновление: {date_str} / {author}\n"

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"✅ Чек-лист создан: {path}")

if __name__ == "__main__":
    main()
