-- Инициализация пользователя для локальной разработки
-- Создаем пользователя если его нет
CREATE USER IF NOT EXISTS 'denis'@'%' IDENTIFIED BY 'Himik28@good';
CREATE USER IF NOT EXISTS 'denis'@'localhost' IDENTIFIED BY 'Himik28@good';

-- Даем полные права на базу yarn_store_local
GRANT ALL PRIVILEGES ON yarn_store_local.* TO 'denis'@'%';
GRANT ALL PRIVILEGES ON yarn_store_local.* TO 'denis'@'localhost';

-- Также даем права на создание тестовых баз (для интеграционных тестов)
GRANT ALL PRIVILEGES ON yarn_store_local_test.* TO 'denis'@'%';
GRANT ALL PRIVILEGES ON yarn_store_local_test.* TO 'denis'@'localhost';

-- Применяем изменения
FLUSH PRIVILEGES;

-- Устанавливаем кодировку для базы по умолчанию
ALTER DATABASE yarn_store_local CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Выводим информацию для логов
SELECT 'Database initialization completed for local development' AS status;