package com.bumbac.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yarn_translations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YarnTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locale;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "yarn_id")
    private Yarn yarn;
}
