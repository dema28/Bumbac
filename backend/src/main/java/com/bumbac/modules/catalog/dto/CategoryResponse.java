package com.bumbac.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {

    @Schema(
            description = "Unique identifier of the category",
            example = "3"
    )
    private Long id;

    @Schema(
            description = "Name of the category",
            example = "Cotton"
    )
    private String name;
}
