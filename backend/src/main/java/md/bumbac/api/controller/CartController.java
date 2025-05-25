package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Cart;
import md.bumbac.api.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(
            @AuthenticationPrincipal Long userId,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken) {
        return ResponseEntity.ok(cartService.getCart(userId, guestToken));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            @AuthenticationPrincipal Long userId,
            @RequestHeader(value = "X-Guest-Token", required = false) String guestToken,
            @RequestBody AddItemRequest body) {
        return ResponseEntity.ok(cartService.addItem(userId, guestToken, body.productId(), body.quantity()));
    }

    public record AddItemRequest(Long productId, int quantity) {}
}