package md.bumbac.api.service;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.*;
import md.bumbac.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;

    private Cart resolveCart(Long userId, String guestToken) {
        if (userId != null) {
            return cartRepo.findByUser_Id(userId)
                    .orElseGet(() -> {
                        User proxy = new User();        // ← создаём только с id
                        proxy.setId(userId);
                        return cartRepo.save(Cart.builder().user(proxy).build());
                    });
        }
        String token = (guestToken == null || guestToken.isBlank())
                ? UUID.randomUUID().toString() : guestToken;
        return cartRepo.findByGuestToken(token)
                .orElseGet(() -> cartRepo.save(Cart.builder().guestToken(token).build()));
    }


    @Transactional
    public Cart addItem(Long userId, String guestToken, Long productId, int qty) {
        Cart cart = resolveCart(userId, guestToken);
        Product product = productRepo.findProductForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStock() < qty) throw new IllegalStateException("Out of stock");

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(product);
                    cart.getItems().add(ci);
                    return ci;
                });
        item.setQuantity(item.getQuantity() + qty);
        product.setStock(product.getStock() - qty);
        return cart;
    }

    public Cart getCart(Long userId, String guestToken) {
        return resolveCart(userId, guestToken);
    }
}