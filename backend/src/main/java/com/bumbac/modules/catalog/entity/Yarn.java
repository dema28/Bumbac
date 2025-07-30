package com.bumbac.modules.catalog.entity;

import com.bumbac.modules.auth.entity.UserFavorite;
import com.bumbac.modules.cart.entity.Color;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String material;
    private Double weight;
    private Double length;

    @Column(name = "pricemdl")
    private Double priceMDL;

    @Column(name = "priceusd")
    private Double priceUSD;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonBackReference
    private Collection collection;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserFavorite> favorites;

    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<YarnTranslation> translations;

    @OneToMany(mappedBy = "yarn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Color> colors;
}
