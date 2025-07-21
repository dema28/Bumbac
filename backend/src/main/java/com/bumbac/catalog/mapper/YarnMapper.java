package com.bumbac.catalog.mapper;

import com.bumbac.catalog.dto.YarnRequest;
import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.entity.Yarn;
import org.springframework.stereotype.Component;

@Component
public class YarnMapper {

    public Yarn toEntity(YarnRequest request) {
        return Yarn.builder()
                .name(request.getName())
                .material(request.getMaterial())
                .weight(request.getWeight())
                .length(request.getLength())
                .priceMDL(request.getPriceMDL())
                .priceUSD(request.getPriceUSD())
                .build();
    }

    public YarnResponse toResponse(Yarn yarn) {
        return YarnResponse.builder()
                .id(yarn.getId())
                .name(yarn.getName())
                .brand(yarn.getBrand() != null ? yarn.getBrand().getName() : null)
                .category(yarn.getCategory() != null ? yarn.getCategory().getName() : null)
                .material(yarn.getMaterial())
                .weight(yarn.getWeight())
                .length(yarn.getLength())
                .priceMDL(yarn.getPriceMDL())
                .priceUSD(yarn.getPriceUSD())
                .build();
    }

}

