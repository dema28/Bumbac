package com.bumbac.core.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Пользователь с id=" + id + " не найден");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
