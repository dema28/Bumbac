package com.bumbac.modules.catalog.mapper;

import com.bumbac.modules.catalog.dto.CategoryRequest;
import com.bumbac.modules.catalog.dto.CategoryResponse;
import com.bumbac.modules.catalog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .build();
    }
}
