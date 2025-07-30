package com.bumbac.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollectionRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(
            description = "Name of the collection",
            example = "Autumn 2025",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(
            description = "Short description of the collection",
            example = "Warm tones and cozy textures for fall knitting"
    )
    private String description;
}
