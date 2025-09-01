package com.bumbac.modules.user.repository;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.user.entity.UserFavorite; // ✅ правильный импорт
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<UserFavorite, Long> {

    /** Получает список избранного пользователя (без пагинации) */
    @Cacheable(value = "favorites", key = "'user_' + #user.id")
    List<UserFavorite> findByUserOrderByAddedAtDesc(User user);

    /** Проверяет существование избранного для пользователя и пряжи */
    @Cacheable(value = "favorites", key = "'exists_' + #userId + '_' + #yarnId")
    boolean existsByUserIdAndYarnId(Long userId, Long yarnId);

    /** Получает избранное по пользователю и пряже */
    @Cacheable(value = "favorites", key = "'user_' + #userId + '_yarn_' + #yarnId")
    Optional<UserFavorite> findByUserIdAndYarnId(Long userId, Long yarnId);

    /** Удаляет избранное по пользователю и пряже */
    @CacheEvict(value = { "favorites" }, allEntries = true)
    void deleteByUserIdAndYarnId(Long userId, Long yarnId);

    /** Количество избранного пользователя */
    @Cacheable(value = "favorites", key = "'count_' + #userId")
    long countByUserId(Long userId);

    /** Избранное с пагинацией (без fetch join — корректно для Page) */
    @Cacheable(value = "favorites", key = "'user_' + #user.id + '_page_' + #pageable.pageNumber + '_size_' + #pageable.pageSize")
    Page<UserFavorite> findByUser(User user, Pageable pageable);

    // Инвалидация кэша при изменениях
    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    <S extends UserFavorite> S save(S entity);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    <S extends UserFavorite> List<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    void deleteById(Long id);

    @Override
    @CacheEvict(value = { "favorites" }, allEntries = true)
    void delete(UserFavorite entity);
}
