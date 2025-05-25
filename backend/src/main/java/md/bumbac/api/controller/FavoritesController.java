package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Product;
import md.bumbac.api.model.User;
import md.bumbac.api.repository.ProductRepository;
import md.bumbac.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Set<Product>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(user.getFavorites());
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long productId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        user.getFavorites().add(product);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long productId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        user.getFavorites().removeIf(p -> p.getId().equals(productId));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
