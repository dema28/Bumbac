package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Category;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Проверка уникальности имени категории
    boolean existsByName(String name);

    // Проверка уникальности имени категории, исключая конкретную категорию
    boolean existsByNameAndIdNot(String name, Long id);

    // Кэшируемые методы для оптимизации
    // Убрали несуществующую связь c.yarns
    @Cacheable(value = "categories", key = "#root.methodName")
    @Query("SELECT DISTINCT c FROM Category c")
    List<Category> findAllWithYarns(); // имя оставлено для совместимости

    @Cacheable(value = "categories", key = "#id")
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Category findByIdWithYarns(@Param("id") Long id); // имя оставлено для совместимости

    // Инвалидация кэша при изменениях
    @Override
    @CacheEvict(value = { "categories" }, allEntries = true)
    <S extends Category> S save(S entity);

    @Override
    @CacheEvict(value = { "categories" }, allEntries = true)
    <S extends Category> List<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = { "categories" }, allEntries = true)
    void deleteById(Long id);

    @Override
    @CacheEvict(value = { "categories" }, allEntries = true)
    void delete(Category entity);

    @Override
    @CacheEvict(value = { "categories" }, allEntries = true)
    void deleteAllById(Iterable<? extends Long> ids);
}
