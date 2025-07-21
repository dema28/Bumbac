package com.bumbac.catalog.service;

import com.bumbac.catalog.dto.YarnRequest;
import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.entity.Brand;
import com.bumbac.catalog.entity.Category;
import com.bumbac.catalog.entity.Collection;
import com.bumbac.catalog.entity.Yarn;
import com.bumbac.catalog.mapper.YarnMapper;
import com.bumbac.catalog.repository.BrandRepository;
import com.bumbac.catalog.repository.CategoryRepository;
import com.bumbac.catalog.repository.CollectionRepository;
import com.bumbac.catalog.repository.YarnRepository;
import com.bumbac.catalog.specification.YarnSpecification;
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
        yarn.setCollection(collection); // ✅ добавлено
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
        Specification<Yarn> spec = Specification.where(null);

        if (category != null) spec = spec.and(YarnSpecification.hasCategory(category));
        if (brand != null) spec = spec.and(YarnSpecification.hasBrand(brand));
        if (material != null) spec = spec.and(YarnSpecification.hasMaterial(material));

        return yarnRepository.findAll(spec)
                .stream()
                .map(yarnMapper::toResponse)
                .toList();
    }
}
