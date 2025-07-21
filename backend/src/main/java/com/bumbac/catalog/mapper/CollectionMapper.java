package com.bumbac.catalog.mapper;

import com.bumbac.catalog.dto.CollectionRequest;
import com.bumbac.catalog.dto.CollectionResponse;
import com.bumbac.catalog.entity.Collection;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionResponse toResponse(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .name(collection.getName())
                .description(collection.getDescription())
                .build();
    }

    public Collection toEntity(CollectionRequest request) {
        return Collection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
