package com.bumbac.modules.user.service;

import com.bumbac.modules.cart.entity.Color;
import com.bumbac.modules.cart.repository.ColorRepository;
import com.bumbac.modules.user.dto.AddToFavoriteRequest;
import com.bumbac.modules.user.dto.FavoriteDTO;
import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.user.entity.UserFavorite;
import com.bumbac.modules.user.entity.UserFavoriteId;
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
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    // Метрики для отслеживания операций
    private final Counter favoritesViewedCounter;
    private final Counter favoritesAddedCounter;
    private final Counter favoritesRemovedCounter;
    private final Counter favoritesErrorCounter;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           FavoriteMapper favoriteMapper,
                           ColorRepository colorRepository,
                           UserRepository userRepository,
                           MeterRegistry meterRegistry) {
        this.favoriteRepository = favoriteRepository;
        this.favoriteMapper = favoriteMapper;
        this.colorRepository = colorRepository;
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
    public void addToFavorites(Long userId, Long colorId) {
        log.info("Добавление в избранное: userId={}, colorId={}", userId, colorId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("Пользователь не найден: {}", userId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                    });

            Color color = colorRepository.findById(colorId)
                    .orElseThrow(() -> {
                        log.warn("Цвет не найден: {}", colorId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Цвет пряжи не найден");
                    });

            if (favoriteRepository.existsByUserIdAndColorId(userId, colorId)) {
                log.warn("Цвет уже в избранном: userId={}, colorId={}", userId, colorId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Цвет уже добавлен в избранное");
            }

            UserFavorite favorite = new UserFavorite();
            favorite.setUser(user);
            favorite.setColor(color);
            favorite.setId(new UserFavoriteId(userId, colorId));
            favorite.setAddedAt(LocalDateTime.now());

            favoriteRepository.save(favorite);

            log.info("Цвет добавлен в избранное: userId={}, colorId={}", userId, colorId);
            favoritesAddedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при добавлении в избранное: userId={}, colorId={}, error={}",
                    userId, colorId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при добавлении в избранное");
        }
    }

    @Transactional
    public void addToFavorites(Long userId, AddToFavoriteRequest request) {
        addToFavorites(userId, request.getColorId());

        // Если есть заметки, обновляем их
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            updateFavoriteNotes(userId, request.getColorId(), request.getNotes());
        }
    }

    @Transactional
    public void removeFromFavorites(Long userId, Long colorId) {
        log.info("Удаление из избранного: userId={}, colorId={}", userId, colorId);
        try {
            if (!userRepository.existsById(userId)) {
                log.warn("Пользователь не найден: {}", userId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
            }

            if (!colorRepository.existsById(colorId)) {
                log.warn("Цвет не найден: {}", colorId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Цвет пряжи не найден");
            }

            if (!favoriteRepository.existsByUserIdAndColorId(userId, colorId)) {
                log.warn("Цвет не найден в избранном: userId={}, colorId={}", userId, colorId);
                favoritesErrorCounter.increment();
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Цвет не найден в избранном");
            }

            favoriteRepository.deleteByUserIdAndColorId(userId, colorId);

            log.info("Цвет удален из избранного: userId={}, colorId={}", userId, colorId);
            favoritesRemovedCounter.increment();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении из избранного: userId={}, colorId={}, error={}",
                    userId, colorId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при удалении из избранного");
        }
    }

    @Transactional(readOnly = true)
    public boolean isInFavorites(Long userId, Long colorId) {
        try {
            boolean exists = favoriteRepository.existsByUserIdAndColorId(userId, colorId);
            log.debug("Проверка избранного: userId={}, colorId={}, exists={}", userId, colorId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Ошибка при проверке избранного: userId={}, colorId={}, error={}",
                    userId, colorId, e.getMessage(), e);
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
    public void updateFavoriteNotes(Long userId, Long colorId, String notes) {
        log.info("Обновление заметок в избранном: userId={}, colorId={}", userId, colorId);
        try {
            UserFavorite favorite = favoriteRepository.findByUserIdAndColorId(userId, colorId)
                    .orElseThrow(() -> {
                        log.warn("Избранное не найдено: userId={}, colorId={}", userId, colorId);
                        favoritesErrorCounter.increment();
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Избранное не найдено");
                    });

            favorite.setNotes(notes);
            favoriteRepository.save(favorite);

            log.info("Заметки обновлены в избранном: userId={}, colorId={}", userId, colorId);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении заметок: userId={}, colorId={}, error={}",
                    userId, colorId, e.getMessage(), e);
            favoritesErrorCounter.increment();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка при обновлении заметок");
        }
    }
}