package com.bumbac.cart.service;

import com.bumbac.auth.entity.User;
import com.bumbac.auth.repository.UserRepository;
import com.bumbac.auth.security.JwtService;
import com.bumbac.cart.dto.AddToCartRequest;
import com.bumbac.cart.dto.UpdateCartRequest;
import com.bumbac.cart.entity.*;
import com.bumbac.cart.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public void addItem(HttpServletRequest request, AddToCartRequest dto) {
        String email = jwtService.extractUsernameFromHeader(request);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(user.getId())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));

        Color color = colorRepository.findById(dto.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not found"));

        CartItemId id = new CartItemId(cart.getId(), color.getId());

        CartItem item = cartItemRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + dto.getQuantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .id(id)
                        .cart(cart)
                        .color(color)
                        .quantity(dto.getQuantity())
                        .addedAt(LocalDateTime.now())
                        .build());

        cartItemRepository.save(item);
    }

    public void updateItem(HttpServletRequest request, UpdateCartRequest dto) {
        String email = jwtService.extractUsernameFromHeader(request);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItemId id = new CartItemId(cart.getId(), dto.getColorId());

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(dto.getQuantity());
        cartItemRepository.save(item);
    }

    public List<CartItem> getItems(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromHeader(request);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return cartItemRepository.findByCart(cart);
    }

    public void clearCart(HttpServletRequest request) {
        String email = jwtService.extractUsernameFromHeader(request);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCart(cart);
    }
}
