package com.bumbac.modules.cart.service;

import com.bumbac.modules.auth.entity.User;
import com.bumbac.modules.auth.repository.UserRepository;
import com.bumbac.modules.auth.security.JwtService;
import com.bumbac.modules.cart.dto.AddToCartRequest;
import com.bumbac.modules.cart.dto.UpdateCartRequest;
import com.bumbac.modules.cart.entity.Cart;
import com.bumbac.modules.cart.entity.CartItem;
import com.bumbac.modules.cart.entity.Color;
import com.bumbac.modules.cart.repository.CartItemRepository;
import com.bumbac.modules.cart.repository.CartRepository;
import com.bumbac.modules.cart.repository.ColorRepository;
import com.bumbac.modules.user.entity.CartItemId;
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
                .orElseThrow(() -> new IllegalArgumentException("Color with ID " + dto.getColorId() + " not found"));


        Cart foundCart = cartRepository.findByUserId(user.getId()).orElse(null);
        Cart cartToUse;

        if (foundCart == null) {
            Cart newCart = new Cart();
            newCart.setUserId(user.getId());
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());

            cartToUse = cartRepository.saveAndFlush(newCart);
            System.out.println("DEBUG >>> cart ID after save = " + cartToUse.getId());
            System.out.println("DEBUG >>> saving new cart...");

        } else {
            cartToUse = foundCart;
        }
        System.out.println("DEBUG >>> requested colorId = " + dto.getColorId());

        Color color = colorRepository.findById(dto.getColorId())
                .orElseThrow(() -> new RuntimeException("Color not found"));

        CartItemId id = new CartItemId(cartToUse.getId(), color.getId());

        CartItem item = cartItemRepository.findById(id)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + dto.getQuantity());
                    return existing;
                })
                .orElseGet(() -> new CartItem(
                        id,
                        cartToUse,
                        color,
                        dto.getQuantity(),
                        LocalDateTime.now()
                ));

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
