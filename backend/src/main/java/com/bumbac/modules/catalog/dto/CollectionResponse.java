package com.bumbac.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectionResponse {

    @Schema(
            description = "Unique identifier of the collection",
            example = "10"
    )
    private Long id;

    @Schema(
            description = "Name of the collection",
            example = "Autumn 2025"
    )
    private String name;

    @Schema(
            description = "Short description of the collection",
            example = "Warm tones and cozy textures for fall knitting"
    )
    private String description;
}
