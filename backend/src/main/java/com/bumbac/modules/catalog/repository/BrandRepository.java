package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Brand;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

  // Проверка уникальности имени бренда
  boolean existsByName(String name);

  // Проверка уникальности имени бренда, исключая конкретный бренд
  boolean existsByNameAndIdNot(String name, Long id);

  // Кэшируемые методы для оптимизации
  @Cacheable(value = "brands", key = "#root.methodName")
  @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.yarns")
  List<Brand> findAllWithYarns();

  @Cacheable(value = "brands", key = "#id")
  @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.yarns WHERE b.id = :id")
  Brand findByIdWithYarns(Long id);

  // Инвалидация кэша при изменениях
  @Override
  @CacheEvict(value = { "brands" }, allEntries = true)
  <S extends Brand> S save(S entity);

  @Override
  @CacheEvict(value = { "brands" }, allEntries = true)
  <S extends Brand> List<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = { "brands" }, allEntries = true)
  void deleteById(Long id);

  @Override
  @CacheEvict(value = { "brands" }, allEntries = true)
  void delete(Brand entity);

  @Override
  @CacheEvict(value = { "brands" }, allEntries = true)
  void deleteAllById(Iterable<? extends Long> ids);
}
