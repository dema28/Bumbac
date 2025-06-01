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
                .brand(request.getBrand())
                .category(request.getCategory())
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
                .brand(yarn.getBrand())
                .category(yarn.getCategory())
                .material(yarn.getMaterial())
                .weight(yarn.getWeight())
                .length(yarn.getLength())
                .priceMDL(yarn.getPriceMDL())
                .priceUSD(yarn.getPriceUSD())
                .build();
    }
}
