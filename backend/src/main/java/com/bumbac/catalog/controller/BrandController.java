package com.bumbac.catalog.controller;

import com.bumbac.catalog.dto.BrandRequest;
import com.bumbac.catalog.dto.BrandResponse;
import com.bumbac.catalog.entity.Brand;
import com.bumbac.catalog.mapper.BrandMapper;
import com.bumbac.catalog.repository.BrandRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BrandController {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Operation(summary = "Get all brands", description = "Returns list of all available yarn brands")
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAll() {
        List<BrandResponse> response = brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create new brand")
    @ApiResponse(responseCode = "200", description = "Brand successfully created")
    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        Brand saved = brandRepository.save(brandMapper.toEntity(request));
        return ResponseEntity.ok(brandMapper.toResponse(saved));
    }

    @Operation(summary = "Delete brand by ID")
    @ApiResponse(responseCode = "204", description = "Brand deleted")
    @ApiResponse(responseCode = "404", description = "Brand not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!brandRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
