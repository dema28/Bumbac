package md.bumbac.api.repository;

import md.bumbac.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий пользователей.
 * Наследуется от JpaRepository, поэтому все CRUD-методы доступны «из коробки».
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Поиск по e-mail — нужен для логина и личного кабинета */
    Optional<User> findByEmail(String email);

    /** Проверка существования e-mail (при регистрации) */
    boolean existsByEmail(String email);
}
