CREATE TABLE carts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guest_token VARCHAR(36),
    user_id BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE cart_items
    ADD COLUMN cart_id BIGINT,
    ADD CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id);
