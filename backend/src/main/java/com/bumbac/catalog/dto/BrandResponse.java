package com.bumbac.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandResponse {

    @Schema(
            description = "Unique identifier of the brand",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the brand",
            example = "Alize"
    )
    private String name;

    @Schema(
            description = "Country where the brand is based",
            example = "Turkey"
    )
    private String country;

    @Schema(
            description = "Official website URL of the brand",
            example = "https://www.alizeyarns.com"
    )
    private String website;
}
