package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    // ---------- системные поля ---------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<CartItem> items;

    private BigDecimal total;
    private String status;          // CREATED / PAID / PACKING / SHIPPED / DONE
    private LocalDateTime createdAt;

    // ---------- данные для службы доставки ---------------------------------

    private String recipientName;   // Получатель
    private String contactPerson;   // Контактное лицо (если отличается)
    private String phoneNumber;
    private String email;

    private String country;
    private String region;          // Район / область
    private String city;
    private String street;
    private String buildingNumber;
    private String postalCode;
}
