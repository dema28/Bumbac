package com.bumbac.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YarnResponse {

    @Schema(
            description = "Unique identifier of the yarn",
            example = "42"
    )
    private Long id;

    @Schema(
            description = "Name of the yarn",
            example = "Super Soft Cotton"
    )
    private String name;

    @Schema(
            description = "Name of the brand",
            example = "Alize"
    )
    private String brand;

    @Schema(
            description = "Name of the category",
            example = "Cotton"
    )
    private String category;

    @Schema(
            description = "Material composition",
            example = "100% Cotton"
    )
    private String material;

    @Schema(
            description = "Weight of the skein in grams",
            example = "50.0"
    )
    private Double weight;

    @Schema(
            description = "Length of the skein in meters",
            example = "150.0"
    )
    private Double length;

    @Schema(
            description = "Price in Moldovan Leu (MDL)",
            example = "39.90"
    )
    private Double priceMDL;

    @Schema(
            description = "Price in US Dollars (USD)",
            example = "2.25"
    )
    private Double priceUSD;
}
