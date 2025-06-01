package com.bumbac.catalog.pattern;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pattern_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternTranslation {

    @EmbeddedId
    private PatternTranslationId id;

    @ManyToOne
    @MapsId("patternId")
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;

    private String name;
    private String description;
}
