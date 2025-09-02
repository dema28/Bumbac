package com.bumbac.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String country;

    private String website;

    // 🔗 связь с Yarn
    @Builder.Default
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Yarn> yarns = new ArrayList<>();
}
