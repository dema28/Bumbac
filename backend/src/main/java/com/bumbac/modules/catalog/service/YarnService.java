package com.bumbac.modules.catalog.service;

import com.bumbac.modules.catalog.dto.YarnRequest;
import com.bumbac.modules.catalog.dto.YarnResponse;
import com.bumbac.modules.catalog.entity.Brand;
import com.bumbac.modules.catalog.entity.Category;
import com.bumbac.modules.catalog.entity.Collection;
import com.bumbac.modules.catalog.entity.Yarn;
import com.bumbac.modules.catalog.mapper.YarnMapper;
import com.bumbac.modules.catalog.repository.BrandRepository;
import com.bumbac.modules.catalog.repository.CategoryRepository;
import com.bumbac.modules.catalog.repository.CollectionRepository;
import com.bumbac.modules.catalog.repository.YarnRepository;
import com.bumbac.modules.catalog.specification.YarnSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YarnService {

    private final YarnRepository yarnRepository;
    private final YarnMapper yarnMapper;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;

    public void create(YarnRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Collection collection = collectionRepository.findById(request.getCollectionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found"));

        Yarn yarn = yarnMapper.toEntity(request);
        yarn.setBrand(brand);
        yarn.setCategory(category);
        yarn.setCollection(collection);
        yarn.setCreatedAt(LocalDateTime.now());

        yarnRepository.save(yarn);
    }

    public List<YarnResponse> getAll() {
        return yarnRepository.findAll()
                .stream()
                .map(yarnMapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!yarnRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Yarn not found");
        }
        yarnRepository.deleteById(id);
    }

    public List<YarnResponse> filter(String category, String brand, String material) {
        Specification<Yarn> spec = YarnSpecification.filterBy(category, brand, material);
        return yarnRepository.findAll(spec)
                .stream()
                .map(yarnMapper::toResponse)
                .toList();
    }
}
