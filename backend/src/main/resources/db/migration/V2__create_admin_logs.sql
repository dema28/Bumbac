CREATE TABLE admin_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT,
    action VARCHAR(255),
    payload TINYTEXT,
    ts TIMESTAMP
);
