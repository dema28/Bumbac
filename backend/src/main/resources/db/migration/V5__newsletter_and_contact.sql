-- Flyway Migration: V5__newsletter_and_contact.sql
-- Generated: 2025-09-03 19:37:21 (FIXED)
-- Description: Inserts test newsletter subscribers and contact messages

SET FOREIGN_KEY_CHECKS=0;

-- NEWSLETTER_SUBSCRIBERS (используем правильную структуру)
-- Структура: user_id, subscribed_at, confirmation_token, confirmed, email, unsubscribed, id
INSERT IGNORE INTO newsletter_subscribers (id, user_id, email, confirmation_token, confirmed, unsubscribed, subscribed_at)
VALUES (201, NULL, 'user1@bumbac.md', 'token-201-abc123', b'1', b'0', NOW());

INSERT IGNORE INTO newsletter_subscribers (id, user_id, email, confirmation_token, confirmed, unsubscribed, subscribed_at)
VALUES (202, NULL, 'user2@bumbac.md', 'token-202-def456', b'1', b'0', NOW());

INSERT IGNORE INTO newsletter_subscribers (id, user_id, email, confirmation_token, confirmed, unsubscribed, subscribed_at)
VALUES (203, 101, 'testuser1@bumbac.md', 'token-203-ghi789', b'0', b'0', NOW());

INSERT IGNORE INTO newsletter_subscribers (id, user_id, email, confirmation_token, confirmed, unsubscribed, subscribed_at)
VALUES (204, NULL, 'user4@bumbac.md', 'token-204-jkl012', b'1', b'1', NOW());

-- CONTACT_MESSAGES (используем правильную структуру)
-- Структура: id, created_at, updated_at, email, file_path, is_read, name, read_at, subject
INSERT IGNORE INTO contact_messages (id, name, email, subject, file_path, is_read, created_at, updated_at, read_at)
VALUES (301, 'Ivan Ivanov', 'ivan@domain.com', 'Shipping Question', '/messages/301.txt', b'0', NOW(), NOW(), NULL);

INSERT IGNORE INTO contact_messages (id, name, email, subject, file_path, is_read, created_at, updated_at, read_at)
VALUES (302, 'Maria Popescu', 'maria@domain.com', 'Payment Issue', '/messages/302.txt', b'0', NOW(), NOW(), NULL);

INSERT IGNORE INTO contact_messages (id, name, email, subject, file_path, is_read, created_at, updated_at, read_at)
VALUES (303, 'John Doe', 'john@domain.com', 'General Feedback', '/messages/303.txt', b'1', NOW(), NOW(), NOW());

INSERT IGNORE INTO contact_messages (id, name, email, subject, file_path, is_read, created_at, updated_at, read_at)
VALUES (304, 'Ana Smirnova', 'ana@domain.com', 'Вопрос о доставке', '/messages/304.txt', b'0', NOW(), NOW(), NULL);

SET FOREIGN_KEY_CHECKS=1;