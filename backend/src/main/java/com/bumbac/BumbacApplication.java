package com.bumbac;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // 👈 добавляем EntityScan для явного указания, где искать @Entity
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.bumbac.modules.auth.entity", // 👤 Пользователи и роли
    "com.bumbac.modules.cart.entity", // 🛒 Корзина и её элементы
    "com.bumbac.modules.catalog.entity", // 🧶 Пряжа и цвета (Color → Yarn связь!)
    "com.bumbac.modules.order.entity", // 📦 Заказы, платежи, возвраты
    "com.bumbac.modules.media.entity", // 🖼️ Медиафайлы (изображения, видео и т.д.)
    "com.bumbac.modules.pattern.entity", // 📖 Схемы вязания
    "com.bumbac.modules.user.entity", // 👤 Профили пользователей и избранное
    "com.bumbac.modules.contact.entity", // 📞 Контакты пользователей
    "com.bumbac.modules.newsletter.entity", // 📰 Подписки на рассылки
    "com.bumbac.shared.entity" // 🛠️ Общие сущности (например, настройки, логи и т.д.)
// 👉 добавляй другие пакеты, если появятся новые модули с @Entity
})
public class BumbacApplication {
  public static void main(String[] args) {
    // Загружаем переменные из .env, если файл существует
    Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing() // 📁 Не выбрасывать ошибку, если .env отсутствует (например, в проде)
        .load();

    // Переносим переменные из .env в системные свойства (Spring их увидит через
    // @Value или конфиг)
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

    // 🚀 Запуск Spring Boot приложения
    SpringApplication.run(BumbacApplication.class, args);
  }
}
