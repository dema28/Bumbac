package com.bumbac.catalog.entity;

import com.bumbac.auth.entity.UserFavorite;
import com.bumbac.cart.entity.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "yarns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Yarn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String brand;
    private String category;
    private String material;
    private Double weight;
    private Double length;
    private Double priceMDL;
    private Double priceUSD;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserFavorite> favorites;

    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<YarnTranslation> translations;

    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Color> colors;


}
