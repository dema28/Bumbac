package com.bumbac.modules.user.repository;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.user.entity.UserFavorite; // ✅ правильный импорт
import com.bumbac.modules.user.entity.UserFavoriteId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<UserFavorite, UserFavoriteId> {

    /** Получает список избранного пользователя (без пагинации) */
    @Cacheable(value = "favorites", key = "'user_' + #user.id")
    List<UserFavorite> findByUserOrderByAddedAtDesc(User user);

    /** Проверяет существование избранного для пользователя и цвета */
    @Cacheable(value = "favorites", key = "'exists_' + #userId + '_color_' + #colorId")
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM UserFavorite f WHERE f.user.id = :userId AND f.color.id = :colorId")
    boolean existsByUserIdAndColorId(@Param("userId") Long userId, @Param("colorId") Long colorId);

    /** Получает избранное по пользователю и цвету */
    @Cacheable(value = "favorites", key = "'user_' + #userId + '_color_' + #colorId")
    @Query("SELECT f FROM UserFavorite f WHERE f.user.id = :userId AND f.color.id = :colorId")
    Optional<UserFavorite> findByUserIdAndColorId(@Param("userId") Long userId, @Param("colorId") Long colorId);

    /** Удаляет избранное по пользователю и цвету */
    @CacheEvict(value = { "favorites" }, allEntries = true)
    @Modifying
    @Query("DELETE FROM UserFavorite f WHERE f.user.id = :userId AND f.color.id = :colorId")
    void deleteByUserIdAndColorId(@Param("userId") Long userId, @Param("colorId") Long colorId);

    /** Количество избранного пользователя */
    @Cacheable(value = "favorites", key = "'count_' + #userId")
    @Query("SELECT COUNT(f) FROM UserFavorite f WHERE f.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /** Избранное с пагинацией */
    @Cacheable(value = "favorites", key = "'user_' + #user.id + '_page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    Page<UserFavorite> findByUser(User user, Pageable pageable);

    /** Найти все избранные записи пользователя для конкретной пряжи (для совместимости) */
    @Query("SELECT f FROM UserFavorite f WHERE f.user.id = :userId AND f.color.yarn.id = :yarnId")
    List<UserFavorite> findByUserIdAndYarnId(@Param("userId") Long userId, @Param("yarnId") Long yarnId);

    /** Проверяет существование любого цвета определенной пряжи в избранном пользователя */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM UserFavorite f WHERE f.user.id = :userId AND f.color.yarn.id = :yarnId")
    boolean existsByUserIdAndYarnId(@Param("userId") Long userId, @Param("yarnId") Long yarnId);

    // Инвалидация кэша при изменениях
    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    <S extends UserFavorite> S save(S entity);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    <S extends UserFavorite> List<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    void deleteById(UserFavoriteId id);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    void delete(UserFavorite entity);
}
