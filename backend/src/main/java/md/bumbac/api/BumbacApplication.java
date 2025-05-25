package md.bumbac.api;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
@Slf4j
public class BumbacApplication {

    public static void main(String[] args) {

        /* ----------------------------------------------------------
         *  1. Загружаем .env-файл с переменными окружения
         *  ---------------------------------------------------------- */
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // не падать, если .env не найден
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            log.debug("ENV {} = {}", entry.getKey(), entry.getValue()); // можно закомментить, если лишний лог
        });

        /* ----------------------------------------------------------
         *  2. Устанавливаем активный Spring-профиль, если он не задан
         *     (можно задать через .env: SPRING_PROFILES_ACTIVE=prod)
         * ---------------------------------------------------------- */
        if (System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME) == null &&
                System.getenv("SPRING_PROFILES_ACTIVE") == null) {
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "mysql");
        }

        /* ----------------------------------------------------------
         *  3. Запускаем Spring Boot приложение
         * ---------------------------------------------------------- */
        SpringApplication.run(BumbacApplication.class, args);

        /* ----------------------------------------------------------
         *  4. Лог о запуске
         * ---------------------------------------------------------- */
        log.info("🚀  Bumbac backend успешно запущен! (active profiles: {})",
                System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
    }
}
