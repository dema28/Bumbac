import os
import re
from datetime import date

def sanitize_filename(title):
    slug = re.sub(r"[^\w]+", "_", title.strip()).strip("_")
    return slug

def ask_steps(prompt):
    raw = input(f"{prompt} (через ;): ").strip()
    parts = [p.strip() for p in raw.split(";")] if ";" in raw else [raw]

    cleaned = []
    for p in parts:
        if not p:
            continue
        # убрать один ведущий маркер списка
        p = re.sub(r"^\s*(?:[-–—*•]|(?:\(?\s*(?:\d+|[ivxlcdm]+|[A-Za-z])\s*\)?[.)]?))\s+",
                   "", p, flags=re.IGNORECASE)
        if p:
            cleaned.append(p)

    return "\n".join(f"{i}. {text}" for i, text in enumerate(cleaned, start=1))

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
    print("🐞 Bug Report Generator / Генератор баг-репортов")

    bug_id = input("🔢 Bug ID (e.g. BUG-001): ").strip()
    title = input("📝 Title / Название: ").strip()
    priority = input("🚦 Priority (High/Medium/Low): ").strip()
    severity = input("⚠️ Severity (Critical/Major/Minor): ").strip()
    status = "Open"
    environment = input("💻 Environment (browser, device, OS): ").strip()
    author = input("👤 Автор баг-репорта: ").strip()
    preconditions = input("🔧 Preconditions / Предусловия: ").strip()
    steps = ask_steps("🔄 Steps to Reproduce / Шаги воспроизведения")
    expected = ask_steps("💭 Expected Result / Ожидаемый результат")
    actual = ask_steps("🚨 Actual Result / Фактический результат")
    attachments = ask_links("📎 Attachments / Вложения (Google Drive)")
    solution = input("🛠️ Suggested Solution / Предлагаемое решение: ").strip()
    additional = input("📌 Additional Info / Дополнительная информация: ").strip()

    slug = sanitize_filename(title)
    filename = f"{bug_id}_{slug}.md"
    folder = os.path.join(os.path.dirname(__file__), "..", "bug_reports")
    folder = os.path.abspath(folder)
    os.makedirs(folder, exist_ok=True)
    path = os.path.join(folder, filename)

    # YAML-блок, обёрнутый в комментарий
    content = "<!--\n---\n"
    content += f"id: {bug_id}\n"
    content += f"title: {title}\n"
    content += f"priority: {priority}\n"
    content += f"severity: {severity}\n"
    content += f"status: {status}\n"
    content += f"environment: {environment}\n"
    content += f"author: {author}\n"
    content += "---\n-->\n\n"

    # Основной контент
    content += f"## 🐞 {bug_id} / {title}\n"
    content += f"### 📅 Date: {date.today()}\n"
    content += f"### 💻 Environment: {environment}\n"
    content += f"### 🚦 Priority: {priority}\n"
    content += f"### ⚠️ Severity: {severity}\n"
    content += f"### 📌 Status: {status}\n\n"
    content += f"### 🔧 Preconditions / Предусловия\n{preconditions}\n\n"
    content += f"### 🔄 Steps to Reproduce / Шаги воспроизведения\n{steps}\n\n"
    content += f"### 💭 Expected Result / Ожидаемый результат\n{expected}\n\n"
    content += f"### 🚨 Actual Result / Фактический результат\n{actual}\n\n"
    
    content += "### 📎 Attachments / Вложения\n"
    if attachments:
        for line in attachments.strip().splitlines():
            content += f"- {line.strip()}\n"
    else:
        content += "- Нет\n"
    content += "\n"
    
    content += f"### 🛠️ Suggested Solution / Предлагаемое решение\n{solution or 'Нет'}\n\n"
    content += f"### 🧩 Additional Information / Дополнительная информация\n{additional or 'Нет'}\n"
    content += f"\n### ✍️ Reported by: {author}\n"

    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"✅ Баг-репорт создан: {path}")

if __name__ == "__main__":
    main()
