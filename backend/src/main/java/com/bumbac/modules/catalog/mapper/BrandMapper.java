package com.bumbac.modules.catalog.mapper;

import com.bumbac.modules.catalog.dto.BrandRequest;
import com.bumbac.modules.catalog.dto.BrandResponse;
import com.bumbac.modules.catalog.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .country(brand.getCountry())
                .website(brand.getWebsite())
                .build();
    }

    public Brand toEntity(BrandRequest request) {
        return Brand.builder()
                .name(request.getName())
                .country(request.getCountry())
                .website(request.getWebsite())
                .build();
    }
}
