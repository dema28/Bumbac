package com.bumbac.modules.admin.service;

import com.bumbac.modules.admin.dto.UserAdminDTO;
import com.bumbac.shared.enums.UserRole;
import com.bumbac.modules.auth.entity.Role;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.RoleRepository;
import com.bumbac.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate; // Добавляем для прямых SQL запросов

    public List<UserAdminDTO> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toUserAdminDTO)
                .toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Попытка удаления пользователя с ID={}", id);

        if (!userRepository.existsById(id)) {
            log.warn("Попытка удалить несуществующего пользователя с ID={}", id);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        // ✅ КАСКАДНОЕ УДАЛЕНИЕ ВСЕХ СВЯЗАННЫХ ЗАПИСЕЙ
        try {
            log.info("Начинаем каскадное удаление для пользователя ID={}", id);

            // 1. Удаляем refresh токены
            int refreshTokens = jdbcTemplate.update("DELETE FROM refresh_token WHERE user_id = ?", id);
            log.debug("Удалено {} refresh токенов для пользователя ID={}", refreshTokens, id);

            // 2. Удаляем элементы корзины
            int cartItems = jdbcTemplate.update(
                    "DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id = ?)", id);
            log.debug("Удалено {} элементов корзины для пользователя ID={}", cartItems, id);

            // 3. Удаляем корзины
            int carts = jdbcTemplate.update("DELETE FROM carts WHERE user_id = ?", id);
            log.debug("Удалено {} корзин для пользователя ID={}", carts, id);

            // 4. Удаляем избранное
            int favorites = jdbcTemplate.update("DELETE FROM user_favorites WHERE user_id = ?", id);
            log.debug("Удалено {} избранных для пользователя ID={}", favorites, id);

            // 5. Обновляем заказы (НЕ удаляем, а обнуляем user_id для сохранения истории)
            int orders = jdbcTemplate.update("UPDATE orders SET user_id = NULL WHERE user_id = ?", id);
            log.debug("Обновлено {} заказов для пользователя ID={} (user_id установлен в NULL)", orders, id);

            // 6. Удаляем роли пользователя
            int userRoles = jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", id);
            log.debug("Удалено {} ролей для пользователя ID={}", userRoles, id);

            // 7. Наконец удаляем самого пользователя
            userRepository.deleteById(id);
            log.info("Пользователь с ID={} успешно удалён со всеми связанными данными", id);

        } catch (Exception e) {
            log.error("Ошибка при каскадном удалении пользователя ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить пользователя: " + e.getMessage(), e);
        }
    }

    @Transactional
    public UserAdminDTO updateRole(Long userId, String roleCode) {
        log.info("Попытка обновления роли пользователя ID={} на роль '{}'", userId, roleCode);

        // Валидация роли через enum
        if (!UserRole.isValidRole(roleCode)) {
            log.error("Некорректный код роли: {}", roleCode);
            throw new IllegalArgumentException("Некорректный код роли: " + roleCode);
        }

        // Проверка существования пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID={} не найден", userId);
                    return new IllegalArgumentException("Пользователь не найден");
                });

        // Проверка существования роли в базе данных
        Role role = roleRepository.findByCode(roleCode.toUpperCase())
                .orElseThrow(() -> {
                    log.error("Роль '{}' не найдена в базе данных", roleCode);
                    return new IllegalArgumentException("Роль не найдена: " + roleCode);
                });

        // Проверка дублирования ролей
        if (user.getRoles().stream().anyMatch(r -> r.getCode().equals(role.getCode()))) {
            log.warn("Пользователь ID={} уже имеет роль '{}'", userId, roleCode);
            throw new IllegalArgumentException("Пользователь уже имеет эту роль");
        }

        // ✅ ИСПРАВЛЕНИЕ: Используем mutable HashSet
        user.setRoles(new HashSet<>(Set.of(role)));
        User savedUser = userRepository.save(user);

        log.info("Роль пользователя ID={} успешно обновлена на '{}'", userId, role.getCode());

        // Возвращаем обновленного пользователя
        return toUserAdminDTO(savedUser);
    }

    private UserAdminDTO toUserAdminDTO(User user) {
        return UserAdminDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoleCodes())
                .build();
    }
}