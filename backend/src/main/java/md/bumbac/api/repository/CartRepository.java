package md.bumbac.api.repository;

import md.bumbac.api.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_Id(Long userId);
    Optional<Cart> findByGuestToken(String token);
}