package com.bumbac.modules.catalog.repository;

import com.bumbac.modules.catalog.entity.Collection;
import com.bumbac.modules.catalog.entity.Yarn;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface YarnRepository extends JpaRepository<Yarn, Long>, JpaSpecificationExecutor<Yarn> {

  // Проверка уникальности имени в рамках коллекции
  boolean existsByNameAndCollection(String name, Collection collection);

  // Проверка уникальности имени в рамках коллекции, исключая конкретную пряжу
  boolean existsByNameAndCollectionAndIdNot(String name, Collection collection, Long id);

  // Кэшируемые методы для оптимизации
  @Cacheable(value = "yarns", key = "#root.methodName")
  @Query("SELECT y FROM Yarn y JOIN FETCH y.brand JOIN FETCH y.category JOIN FETCH y.collection")
  List<Yarn> findAllWithRelations();

  @Cacheable(value = "yarns", key = "#id")
  @Query("SELECT y FROM Yarn y JOIN FETCH y.brand JOIN FETCH y.category JOIN FETCH y.collection WHERE y.id = :id")
  Yarn findByIdWithRelations(@Param("id") Long id);

  // Инвалидация кэша при изменениях
  @Override
  @CacheEvict(value = { "yarns" }, allEntries = true)
  <S extends Yarn> S save(S entity);

  @Override
  @CacheEvict(value = { "yarns" }, allEntries = true)
  <S extends Yarn> List<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = { "yarns" }, allEntries = true)
  void deleteById(Long id);

  @Override
  @CacheEvict(value = { "yarns" }, allEntries = true)
  void delete(Yarn entity);

  @Override
  @CacheEvict(value = { "yarns" }, allEntries = true)
  void deleteAllById(Iterable<? extends Long> ids);

    // ✅ Быстрая проверка использования бренда
    boolean existsByBrand_Id(Long brandId);

    // ✅ Опционально для логов
    long countByBrand_Id(Long brandId);
}
