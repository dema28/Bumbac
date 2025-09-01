package com.bumbac.modules.cart.repository;

import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.entity.CartItemId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {

  @Cacheable(value = "cartItems", key = "#cart.id")
  @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.color c JOIN FETCH c.yarn y WHERE ci.cart = :cart")
  List<CartItem> findByCart(@Param("cart") Cart cart);

  @Modifying
  @Transactional
  @CacheEvict(value = { "cartItems", "carts" }, allEntries = true)
  @Query("DELETE FROM CartItem ci WHERE ci.cart = :cart")
  void deleteByCart(@Param("cart") Cart cart);

  @Override
  @CacheEvict(value = { "cartItems", "carts" }, allEntries = true)
  <S extends CartItem> S save(S entity);

  @Override
  @CacheEvict(value = { "cartItems", "carts" }, allEntries = true)
  void delete(CartItem entity);

  @Override
  @CacheEvict(value = { "cartItems", "carts" }, allEntries = true)
  void deleteById(CartItemId id);
}
