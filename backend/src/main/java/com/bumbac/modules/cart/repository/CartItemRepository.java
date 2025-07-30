package com.bumbac.modules.cart.repository;

import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.user.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
}


