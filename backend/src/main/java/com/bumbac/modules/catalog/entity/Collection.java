package com.bumbac.modules.catalog.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Builder.Default
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference
    private List<Yarn> yarns = new ArrayList<>();
}
