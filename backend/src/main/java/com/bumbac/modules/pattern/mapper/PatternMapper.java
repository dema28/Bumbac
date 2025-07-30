package com.bumbac.modules.pattern.mapper;

import com.bumbac.modules.catalog.entity.YarnTranslation;
import com.bumbac.modules.pattern.dto.PatternDTO;
import com.bumbac.modules.pattern.entity.Pattern;
import com.bumbac.modules.pattern.entity.PatternTranslation;
import org.springframework.stereotype.Component;

@Component
public class PatternMapper {

    public PatternDTO toDto(Pattern pattern, String lang) {
        PatternTranslation translation = pattern.getTranslations().stream()
                .filter(t -> t.getId().getLocale().equalsIgnoreCase(lang))
                .findFirst()
                .orElseGet(() -> fallbackToEn(pattern));

        String yarnName = pattern.getYarn().getTranslations().stream()
                .filter(t -> t.getId().getLocale().equalsIgnoreCase(lang))
                .map(YarnTranslation::getName)
                .findFirst()
                .orElse("Unnamed Yarn");

        return PatternDTO.builder()
                .code(pattern.getCode())
                .name(translation.getName())
                .description(translation.getDescription())
                .pdfUrl(pattern.getPdfUrl())
                .difficulty(pattern.getDifficulty().name())
                .yarnName(yarnName)
                .build();
    }

    private PatternTranslation fallbackToEn(Pattern pattern) {
        return pattern.getTranslations().stream()
                .filter(t -> t.getId().getLocale().equalsIgnoreCase("en"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Translation not found"));
    }
}
