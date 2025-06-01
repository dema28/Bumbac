package com.bumbac.catalog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YarnResponse {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private String material;
    private Double weight;
    private Double length;
    private Double priceMDL;
    private Double priceUSD;
}
