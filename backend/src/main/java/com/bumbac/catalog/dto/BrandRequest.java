package com.bumbac.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class BrandRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(
            description = "Name of the brand",
            example = "Alize",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 100)
    @Schema(
            description = "Country where the brand is based",
            example = "Turkey"
    )
    private String country;

    @Size(max = 255)
    @Schema(
            description = "Official website URL of the brand",
            example = "https://www.alizeyarns.com"
    )
    private String website;
}
