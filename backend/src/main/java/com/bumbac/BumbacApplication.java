package com.bumbac;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // 👈 добавляем EntityScan для явного указания, где искать @Entity
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.bumbac.auth.entity",      // 👤 Пользователи и роли
        "com.bumbac.cart.entity",      // 🛒 Корзина и её элементы
        "com.bumbac.catalog.entity",   // 🧶 Пряжа и цвета (Color → Yarn связь!)
        "com.bumbac.order.entity",     // 📦 Заказы, платежи, возвраты
        "com.bumbac.catalog.media", // 🖼️ Медиафайлы (изображения, видео и т.д.)
        "com.bumbac.catalog.pattern", // 📖 Схемы вязания
        "com.bumbac.user.entity",      // 👤 Профили пользователей
        "com.bumbac.contact.entity", // 📞 Контакты пользователей
        "com.bumbac.newsletter.entity" // 📰 Подписки на рассылки
        // 👉 добавляй другие пакеты, если появятся новые модули с @Entity
})
public class BumbacApplication {
    public static void main(String[] args) {
        // Загружаем переменные из .env, если файл существует
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()   // 📁 Не выбрасывать ошибку, если .env отсутствует (например, в проде)
                .load();

        // Переносим переменные из .env в системные свойства (Spring их увидит через @Value или конфиг)
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        // 🚀 Запуск Spring Boot приложения
        SpringApplication.run(BumbacApplication.class, args);
    }
}
