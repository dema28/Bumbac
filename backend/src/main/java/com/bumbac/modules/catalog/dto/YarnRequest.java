package com.bumbac.modules.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class YarnRequest {

    @NotBlank(message = "Name is required")
    @Schema(
            description = "Name of the yarn",
            example = "Super Soft Cotton",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @NotBlank(message = "Material is required")
    @Schema(
            description = "Material of the yarn",
            example = "100% Cotton",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String material;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    @Schema(
            description = "Weight of the skein in grams",
            example = "50.0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double weight;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be greater than 0")
    @Schema(
            description = "Length of the skein in meters",
            example = "150.0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double length;

    @NotNull(message = "Price in MDL is required")
    @PositiveOrZero(message = "Price in MDL must be zero or greater")
    @Schema(
            description = "Price in Moldovan Leu",
            example = "39.90",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double priceMDL;

    @NotNull(message = "Price in USD is required")
    @PositiveOrZero(message = "Price in USD must be zero or greater")
    @Schema(
            description = "Price in US Dollars",
            example = "2.25",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double priceUSD;

    @NotNull(message = "Brand ID is required")
    @Schema(
            description = "ID of the associated brand",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long brandId;

    @NotNull(message = "Category ID is required")
    @Schema(
            description = "ID of the associated category",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoryId;

    @NotNull(message = "Collection ID is required")
    @Schema(
            description = "ID of the associated collection",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long collectionId;
}
