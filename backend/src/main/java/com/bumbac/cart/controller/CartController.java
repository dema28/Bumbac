package com.bumbac.cart.controller;

import com.bumbac.cart.dto.AddToCartRequest;
import com.bumbac.cart.dto.UpdateCartRequest;
import com.bumbac.cart.entity.CartItem;
import com.bumbac.cart.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addItem(HttpServletRequest request, @RequestBody AddToCartRequest dto) {
        cartService.addItem(request, dto);
        return ResponseEntity.ok("Added to cart");
    }

    @PutMapping
    public ResponseEntity<?> updateItem(HttpServletRequest request, @RequestBody UpdateCartRequest dto) {
        cartService.updateItem(request, dto);
        return ResponseEntity.ok("Cart updated");
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getItems(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getItems(request));
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(HttpServletRequest request) {
        cartService.clearCart(request);
        return ResponseEntity.ok("Cart cleared");
    }
}
