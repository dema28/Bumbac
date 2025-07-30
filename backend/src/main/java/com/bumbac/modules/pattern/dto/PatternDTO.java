package com.bumbac.modules.pattern.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatternDTO {
    private String code;
    private String name;
    private String description;
    private String pdfUrl;
    private String difficulty;
    private String yarnName;
}
