package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String guestToken;            // UUID for guest carts
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;                    // null for guest
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
    private Instant updatedAt;

    @PreUpdate @PrePersist
    private void touch() { updatedAt = Instant.now(); }
}