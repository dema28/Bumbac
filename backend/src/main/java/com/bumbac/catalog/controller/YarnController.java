package com.bumbac.catalog.controller;

import com.bumbac.catalog.dto.YarnRequest;
import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.service.YarnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yarns")
@RequiredArgsConstructor
public class YarnController {

    private final YarnService yarnService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody YarnRequest request) {
        yarnService.create(request);
        return ResponseEntity.ok("Yarn created");
    }

    @GetMapping
    public ResponseEntity<List<YarnResponse>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String material
    ) {
        if (category != null || brand != null || material != null) {
            return ResponseEntity.ok(yarnService.filter(category, brand, material));
        }

        return ResponseEntity.ok(yarnService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        yarnService.delete(id);
        return ResponseEntity.ok("Yarn deleted");
    }
}
