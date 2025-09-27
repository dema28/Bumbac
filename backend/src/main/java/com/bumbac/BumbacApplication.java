package com.bumbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Главный класс Spring Boot приложения Bumbac
 * Yarn Store Backend - система управления магазином пряжи
 * - Управление каталогом товаров (пряжа, инструменты, схемы)
 * - Система пользователей и аутентификации
 * - Корзина и заказы
 * - Администрирование и отчеты
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.bumbac.core",     // Основная конфигурация и общие компоненты
                "com.bumbac.modules",  // Бизнес-модули
                "com.bumbac.shared"    // Общие сервисы и утилиты
        })
@EnableConfigurationProperties
@EnableCaching
@EnableAsync
public class BumbacApplication {

    public static void main(String[] args) {
        // Настройка системных свойств для корректной работы
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.awt.headless", "true");

        // Запуск Spring Boot приложения
        SpringApplication.run(BumbacApplication.class, args);
    }
}










//import io.github.cdimascio.dotenv.Dotenv;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan; // 👈 добавляем EntityScan для явного указания, где искать @Entity
//import org.springframework.context.annotation.ComponentScan;
//@SpringBootApplication
//@EntityScan(basePackages = {
//        "com.bumbac.modules.auth.entity", // 👤 Пользователи и роли
//        "com.bumbac.modules.cart.entity", // 🛒 Корзина и её элементы
//        "com.bumbac.modules.catalog.entity", // 🧶 Пряжа и цвета (Color → Yarn связь!)
//        "com.bumbac.modules.order.entity", // 📦 Заказы, платежи, возвраты
//        "com.bumbac.modules.media.entity", // 🖼️ Медиафайлы (изображения, видео и т.д.)
//        "com.bumbac.modules.pattern.entity", // 📖 Схемы вязания
//        "com.bumbac.modules.user.entity", // 👤 Профили пользователей и избранное
//        "com.bumbac.modules.contact.entity", // 📞 Контакты пользователей
//        "com.bumbac.modules.newsletter.entity", // 📰 Подписки на рассылки
//        "com.bumbac.shared.entity" // 🛠️ Общие сущности (например, настройки, логи и т.д.)
//// 👉 добавляй другие пакеты, если появятся новые модули с @Entity
//public class BumbacApplication {
//    public static void main(String[] args) {
////        // Загружаем переменные из local.properties, если файл существует
////        Dotenv dotenv = Dotenv.configure()
////                .directory("envs/local")                  // 📁 Путь до файла
////                .filename("local.properties")             // 📄 Имя файла
////                .ignoreIfMissing()                        // ❗ Не выбрасывать ошибку, если файла нет
////                .load();
////
////        // Переносим переменные из local.properties в системные свойства (Spring их увидит через @Value или конфиг)
////        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
//
//        // 🚀 Запуск Spring Boot приложения
//        SpringApplication.run(BumbacApplication.class, args);
//    }
//}
