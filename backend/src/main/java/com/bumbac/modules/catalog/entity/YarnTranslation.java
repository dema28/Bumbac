package com.bumbac.modules.catalog.entity;

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

    @EmbeddedId
    private YarnTranslationId id;

    @Column(nullable = false)
    private String name;

    private String description;

    @MapsId("yarnId")
    @ManyToOne
    @JoinColumn(name = "yarn_id")
    private Yarn yarn;
}
