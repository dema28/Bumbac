package com.bumbac.catalog.service;

import com.bumbac.catalog.dto.YarnRequest;
import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.entity.Yarn;
import com.bumbac.catalog.mapper.YarnMapper;
import com.bumbac.catalog.repository.YarnRepository;
import com.bumbac.catalog.specification.YarnSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YarnService {

    private final YarnRepository yarnRepository;
    private final YarnMapper yarnMapper;

    public void create(YarnRequest request) {
        Yarn yarn = yarnMapper.toEntity(request);
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
