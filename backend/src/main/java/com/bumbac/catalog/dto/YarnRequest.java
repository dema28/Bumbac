package com.bumbac.catalog.dto;

import lombok.Data;

@Data
public class YarnRequest {
    private String name;
    private String brand;
    private String category;
    private String material;
    private Double weight;
    private Double length;
    private Double priceMDL;
    private Double priceUSD;
}
