package com.bumbac.modules.cart.repository;

import com.bumbac.modules.cart.entity.Cart;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

  @Cacheable(value = "carts", key = "#userId")
  @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
  Optional<Cart> findByUserId(@Param("userId") Long userId);

  @Override
  @CacheEvict(value = { "carts", "cartItems" }, allEntries = true)
  @NonNull
  <S extends Cart> S save(@NonNull S entity);

  @Override
  @CacheEvict(value = { "carts", "cartItems" }, allEntries = true)
  void delete(@NonNull Cart entity);

  @Override
  @CacheEvict(value = { "carts", "cartItems" }, allEntries = true)
  void deleteById(@NonNull Long id);
}
