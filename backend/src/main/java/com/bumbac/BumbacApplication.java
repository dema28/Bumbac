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