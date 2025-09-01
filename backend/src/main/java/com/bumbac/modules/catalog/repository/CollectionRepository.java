package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Collection;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

  // Проверка уникальности имени коллекции
  boolean existsByName(String name);

  // Проверка уникальности имени коллекции, исключая конкретную коллекцию
  boolean existsByNameAndIdNot(String name, Long id);

  // Кэшируемые методы для оптимизации
  @Cacheable(value = "collections", key = "#root.methodName")
  @Query("SELECT c FROM Collection c LEFT JOIN FETCH c.yarns")
  List<Collection> findAllWithYarns();

  @Cacheable(value = "collections", key = "#id")
  @Query("SELECT c FROM Collection c LEFT JOIN FETCH c.yarns WHERE c.id = :id")
  Collection findByIdWithYarns(Long id);

  // Инвалидация кэша при изменениях
  @Override
  @CacheEvict(value = { "collections" }, allEntries = true)
  <S extends Collection> S save(S entity);

  @Override
  @CacheEvict(value = { "collections" }, allEntries = true)
  <S extends Collection> List<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = { "collections" }, allEntries = true)
  void deleteById(Long id);

  @Override
  @CacheEvict(value = { "collections" }, allEntries = true)
  void delete(Collection entity);

  @Override
  @CacheEvict(value = { "collections" }, allEntries = true)
  void deleteAllById(Iterable<? extends Long> ids);
}
