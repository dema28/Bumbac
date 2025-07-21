package com.bumbac.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(
            description = "Name of the category",
            example = "Cotton",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;
}
