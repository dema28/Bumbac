-- Добавляем флаг подтверждения email в таблицу пользователей
ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;

-- Создаём таблицу токенов подтверждения email
CREATE TABLE email_verification_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);
