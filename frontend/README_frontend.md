# 🌐 Bumbac Frontend

В этой папке находится весь HTML-шаблон сайта магазина пряжи Bumbac.

---

## 📁 Структура

```plaintext
frontend/
├── index.html                  # Главная страница
├── Login.html, Register.html  # Аутентификация
├── Dashboard.html              # Кабинет пользователя
├── Yarn-collection.html       # Каталог пряжи
├── css/                        # Tailwind + Remixicon стили
├── js/                         # Модули для UI/UX
├── assets/                     # Иконки, шрифты, изображения
├── tailwind.config.js         # Tailwind-конфиг
└── tailwindcss.exe            # Windows-сборка Tailwind (можно заменить)
```

---

## ⚙️ Как работать с шаблоном

### 🖥 1. Открыть прямо в браузере (без сервера)

```bash
cd frontend/
start index.html
```

(или просто открыть через проводник двойным щелчком)

---

### 🌀 2. Редактировать HTML/JS/CSS напрямую

- Все страницы оформлены вручную
- Подключение языков — через JS-модули (`js/language-switcher.module.js`)
- Стили — Tailwind CSS + кастомные (`css/style.css`, `fonts.css`)

---

### 🛠 3. Использовать Tailwind CLI (альтернатива `.exe`)

#### 🔹 Установить Tailwind CLI:

```bash
npm install -D tailwindcss
npx tailwindcss init
```

#### 🔹 Скомпилировать CSS вручную:

```bash
npx tailwindcss -i ./css/input.tailwind.css -o ./css/tailwind.css --watch
```

---

## 🔁 Рекомендации

- `tailwindcss.exe` можно удалить и заменить на `npx tailwindcss` (через Node.js)
- JS-файлы удобно разносить по компонентам, если будешь подключать шаблонизатор
- Можно собрать в полноценный SPA позже или интегрировать как шаблон на Java backend

---

## 🧶 Автор шаблона

Проект Bumbac — [Denis Novicov](https://github.com/dema28)
