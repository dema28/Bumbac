package com.bumbac.modules.user.service;

import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.catalog.repository.YarnRepository;
import com.bumbac.modules.user.dto.AddToFavoriteRequest;
import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.user.entity.UserFavorite;
import com.bumbac.modules.user.mapper.FavoriteMapper;
import com.bumbac.modules.user.repository.FavoriteRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;
    private final YarnRepository yarnRepository;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    // Метрики для отслеживания операций
    private final Counter favoritesViewedCounter;
    private final Counter favoritesAddedCounter;
    private final Counter favoritesRemovedCounter;
    private final Counter favoritesErrorCounter;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           FavoriteMapper favoriteMapper,
                           YarnRepository yarnRepository,
                           UserRepository userRepository,
                           MeterRegistry meterRegistry) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.yarnRepository = yarnRepository;
        this.userRepository = userRepository;
        this.meterRegistry = meterRegistry;

        // Инициализация метрик
        this.favoritesViewedCounter = Counter.builder("favorites.operations.viewed")
                .description("Количество просмотров избранного")
                .register(meterRegistry);
        this.favoritesAddedCounter = Counter.builder("favorites.operations.added")
                .description("Количество добавлений в избранное")
                .register(meterRegistry);
        this.favoritesRemovedCounter = Counter.builder("favorites.operations.removed")
                .description("Количество удалений из избранного")
                .register(meterRegistry);
        this.favoritesErrorCounter = Counter.builder("favorites.operations.errors")
                .description("Количество ошибок при операциях с избранным")
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    public List<FavoriteDTO> getFavoritesForUser(Long userId) {
        log.debug("Получение избранного для пользователя: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("Пользователь не найден: {}", userId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                    });

            // ✅ без пагинации — корректная сигнатура из репозитория
            List<UserFavorite> favorites = favoriteRepository.findByUserOrderByAddedAtDesc(user);
            List<FavoriteDTO> dtos = favoriteMapper.toDtoList(favorites);

            log.debug("Получено {} избранных товаров для пользователя {}", dtos.size(), userId);
            favoritesViewedCounter.increment();
            return dtos;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении избранного для пользователя {}: {}", userId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при получении избранного");
        }
    }

    @Transactional
    public void addToFavorites(Long userId, Long yarnId) {
        log.info("Добавление в избранное: userId={}, yarnId={}", userId, yarnId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("Пользователь не найден: {}", userId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                    });

            Yarn yarn = yarnRepository.findById(yarnId)
                    .orElseThrow(() -> {
                        log.warn("Пряжа не найдена: {}", yarnId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пряжа не найдена");
                    });

            if (favoriteRepository.existsByUserIdAndYarnId(userId, yarnId)) {
                log.warn("Товар уже в избранном: userId={}, yarnId={}", userId, yarnId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Товар уже добавлен в избранное");
            }

            // ✅ создаём обычную сущность без EmbeddedId
            UserFavorite favorite = new UserFavorite();
            favorite.setUser(user);
            favorite.setYarn(yarn);
            favorite.setAddedAt(LocalDateTime.now()); // у тебя поле есть в сущности

            favoriteRepository.save(favorite);

            log.info("Товар добавлен в избранное: userId={}, yarnId={}", userId, yarnId);
            favoritesAddedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при добавлении в избранное: userId={}, yarnId={}, error={}",
                    userId, yarnId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при добавлении в избранное");
        }
    }

    @Transactional
    public void addToFavorites(Long userId, AddToFavoriteRequest request) {
        addToFavorites(userId, request.getYarnId());
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long yarnId) {
        log.info("Удаление из избранного: userId={}, yarnId={}", userId, yarnId);
        try {
            if (!userRepository.existsById(userId)) {
                log.warn("Пользователь не найден: {}", userId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
            }

            if (!yarnRepository.existsById(yarnId)) {
                log.warn("Пряжа не найдена: {}", yarnId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пряжа не найдена");
            }

            if (!favoriteRepository.existsByUserIdAndYarnId(userId, yarnId)) {
                log.warn("Товар не найден в избранном: userId={}, yarnId={}", userId, yarnId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден в избранном");
            }

            favoriteRepository.deleteByUserIdAndYarnId(userId, yarnId);

            log.info("Товар удален из избранного: userId={}, yarnId={}", userId, yarnId);
            favoritesRemovedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении из избранного: userId={}, yarnId={}, error={}",
                    userId, yarnId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при удалении из избранного");
        }
    }

    @Transactional(readOnly = true)
    public boolean isInFavorites(Long userId, Long yarnId) {
        try {
            boolean exists = favoriteRepository.existsByUserIdAndYarnId(userId, yarnId);
            log.debug("Проверка избранного: userId={}, yarnId={}, exists={}", userId, yarnId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Ошибка при проверке избранного: userId={}, yarnId={}, error={}",
                    userId, yarnId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public long getFavoritesCount(Long userId) {
        try {
            long count = favoriteRepository.countByUserId(userId);
            log.debug("Количество избранного для пользователя {}: {}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("Ошибка при подсчете избранного для пользователя {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    @Transactional
    public void updateFavoriteNotes(Long userId, Long yarnId, String notes) {
        log.info("Обновление заметок в избранном: userId={}, yarnId={}", userId, yarnId);
        try {
            UserFavorite favorite = favoriteRepository.findByUserIdAndYarnId(userId, yarnId)
                    .orElseThrow(() -> {
                        log.warn("Избранное не найдено: userId={}, yarnId={}", userId, yarnId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Избранное не найдено");
                    });

            // У тебя в сущности уже есть поле notes — обновляем
            favorite.setNotes(notes);
            favoriteRepository.save(favorite);

            log.info("Заметки обновлены в избранном: userId={}, yarnId={}", userId, yarnId);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении заметок: userId={}, yarnId={}, error={}",
                    userId, yarnId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении заметок");
        }
    }
}
