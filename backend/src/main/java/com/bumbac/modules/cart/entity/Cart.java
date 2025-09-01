package com.bumbac.modules.cart.entity;

import com.bumbac.shared.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "carts", indexes = {
    @Index(name = "idx_cart_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Корзина покупок пользователя")
public class Cart extends BaseEntity {

  @Column(name = "user_id", nullable = false)
  @Schema(description = "ID пользователя владельца корзины", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long userId;

  @JsonManagedReference
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Schema(description = "Список товаров в корзине")
  private List<CartItem> items;
}
