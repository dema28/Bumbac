package com.bumbac.modules.user.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.user.dto.UpdateUserDto;
import com.bumbac.modules.user.dto.UserProfileRequest;
import com.bumbac.modules.user.dto.UserProfileResponse;
import com.bumbac.modules.user.mapper.UserMapper;
import com.bumbac.modules.user.repository.FavoriteRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // по умолчанию readOnly=false; на чтение ниже ставим @Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MeterRegistry meterRegistry;

    // Метрики — НЕ final, инициализируем после инъекции зависимостей
    private Counter profileViewedCounter;
    private Counter profileUpdatedCounter;
    private Counter passwordChangedCounter;
    private Counter userDeletedCounter;
    private Counter userErrorCounter;

    @PostConstruct
    void initMetrics() {
        profileViewedCounter = Counter.builder("user.operations.profile_viewed")
                .description("Количество просмотров профилей пользователей")
                .register(meterRegistry);
        profileUpdatedCounter = Counter.builder("user.operations.profile_updated")
                .description("Количество обновлений профилей пользователей")
                .register(meterRegistry);
        passwordChangedCounter = Counter.builder("user.operations.password_changed")
                .description("Количество смен паролей")
                .register(meterRegistry);
        userDeletedCounter = Counter.builder("user.operations.user_deleted")
                .description("Количество удалений пользователей")
                .register(meterRegistry);
        userErrorCounter = Counter.builder("user.operations.errors")
                .description("Количество ошибок при операциях с пользователями")
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        log.debug("Получение текущего пользователя: {}", username);
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.warn("Пользователь не найден: {}", username);
                        userErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                    });

            log.debug("Пользователь найден: id={}, email={}", user.getId(), user.getEmail());
            profileViewedCounter.increment();
            return user;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя {}: {}", username, e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении пользователя");
        }
    }

    @Transactional
    public void updateUserProfile(String username, UpdateUserDto dto) {
        log.info("Обновление профиля пользователя: {}", username);
        try {
            User user = getCurrentUser(username);

            if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(dto.getEmail())) {
                    log.warn("Email уже существует: {}", dto.getEmail());
                    userErrorCounter.increment();
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email уже используется");
                }
                user.setEmail(dto.getEmail());
            }

            if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null)  user.setLastName(dto.getLastName());
            if (dto.getPhone() != null)     user.setPhone(dto.getPhone());

            User savedUser = userRepository.save(user);

            log.info("Профиль пользователя обновлен: id={}, email={}", savedUser.getId(), savedUser.getEmail());
            profileUpdatedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении профиля пользователя {}: {}", username, e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении профиля");
        }
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Смена пароля пользователя: {}", username);
        try {
            User user = getCurrentUser(username);

            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                log.warn("Неверный старый пароль для пользователя: {}", username);
                userErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный старый пароль");
            }

            if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
                log.warn("Новый пароль совпадает со старым для пользователя: {}", username);
                userErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Новый пароль должен отличаться от старого");
            }

            user.setPasswordHash(passwordEncoder.encode(newPassword));
            User savedUser = userRepository.save(user);

            log.info("Пароль пользователя изменен: id={}, email={}", savedUser.getId(), savedUser.getEmail());
            passwordChangedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при смене пароля пользователя {}: {}", username, e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при смене пароля");
        }
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(HttpServletRequest request) {
        String clientIP = getClientIP(request);
        log.debug("Запрос профиля от IP: {}", clientIP);
        try {
            String username = extractUsernameFromRequest(request);
            User user = getCurrentUser(username);
            UserProfileResponse response = userMapper.toDto(user);

            log.debug("Профиль получен: id={}, email={}", user.getId(), user.getEmail());
            profileViewedCounter.increment();
            return response;

        } catch (Exception e) {
            log.error("Ошибка при получении профиля: {}", e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении профиля");
        }
    }

    @Transactional
    public UserProfileResponse updateProfile(HttpServletRequest request, UserProfileRequest dto) {
        String clientIP = getClientIP(request);
        log.info("Обновление профиля от IP: {}", clientIP);
        try {
            String username = extractUsernameFromRequest(request);
            User user = getCurrentUser(username);
            userMapper.update(user, dto);
            User savedUser = userRepository.save(user);
            UserProfileResponse response = userMapper.toDto(savedUser);

            log.info("Профиль обновлен: id={}, email={}", savedUser.getId(), savedUser.getEmail());
            profileUpdatedCounter.increment();
            return response;

        } catch (Exception e) {
            log.error("Ошибка при обновлении профиля: {}", e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении профиля");
        }
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAll() {
        log.debug("Получение всех пользователей");
        try {
            List<User> users = userRepository.findAll();
            List<UserProfileResponse> responses = userMapper.toDtoList(users);

            log.debug("Получено {} пользователей", responses.size());
            profileViewedCounter.increment();
            return responses;

        } catch (Exception e) {
            log.error("Ошибка при получении всех пользователей: {}", e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении пользователей");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Удаление пользователя: id={}", id);
        try {
            if (!userRepository.existsById(id)) {
                log.warn("Пользователь не найден для удаления: id={}", id);
                userErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
            }

            userRepository.deleteById(id);

            log.info("Пользователь удален: id={}", id);
            userDeletedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя {}: {}", id, e.getMessage(), e);
            userErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при удалении пользователя");
        }
    }

    @Transactional(readOnly = true)
    public int getFavoritesCount(Long userId) {
        try {
            long count = favoriteRepository.countByUserId(userId);
            log.debug("Количество избранного для пользователя {}: {}", userId, count);
            return (int) count;
        } catch (Exception e) {
            log.error("Ошибка при подсчете избранного для пользователя {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    // Вспомогательные методы
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        return request.getRemoteAddr();
    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        // TODO: реализовать извлечение username из JWT токена
        return "user@example.com";
    }
}
