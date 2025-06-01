package com.bumbac.cart.repository;

import com.bumbac.cart.entity.Cart;
import com.bumbac.cart.entity.CartItem;
import com.bumbac.cart.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
}


