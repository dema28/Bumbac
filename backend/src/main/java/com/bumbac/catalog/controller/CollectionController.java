package com.bumbac.catalog.controller;

import com.bumbac.catalog.dto.CollectionRequest;
import com.bumbac.catalog.dto.CollectionResponse;
import com.bumbac.catalog.entity.Collection;
import com.bumbac.catalog.mapper.CollectionMapper;
import com.bumbac.catalog.repository.CollectionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> getAll() {
        List<CollectionResponse> response = collectionRepository.findAll().stream()
                .map(collectionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> create(@Valid @RequestBody CollectionRequest request) {
        Collection saved = collectionRepository.save(collectionMapper.toEntity(request));
        return ResponseEntity.ok(collectionMapper.toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!collectionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        collectionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
