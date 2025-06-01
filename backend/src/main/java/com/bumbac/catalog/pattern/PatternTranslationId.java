package com.bumbac.catalog.pattern;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PatternTranslationId implements Serializable {
    private Long patternId;
    private String locale;
}
