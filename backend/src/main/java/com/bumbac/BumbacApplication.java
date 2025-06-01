package com.bumbac;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BumbacApplication {
    public static void main(String[] args) {
        // Загружаем .env, если файл существует
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()   // Не выбрасывать ошибку, если .env нет (например, в продакшене)
                .load();

        // Копируем переменные в системные свойства, чтобы Spring их увидел
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(BumbacApplication.class, args);
    }
}
