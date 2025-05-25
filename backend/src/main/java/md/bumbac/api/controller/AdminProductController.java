package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Product;
import md.bumbac.api.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONTENT_MANAGER') or hasRole('ADMIN')")
public class AdminProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product updated) {
        return productRepository.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setPrice(updated.getPrice());
            p.setSku(updated.getSku());
            p.setColorName(updated.getColorName());
            p.setHexColor(updated.getHexColor());
            p.setFullDescription(updated.getFullDescription());
            p.setShortDescription(updated.getShortDescription());
            p.setUnitType(updated.getUnitType());
            p.setAvailabilityStatus(updated.getAvailabilityStatus());
            p.setGalleryImages(updated.getGalleryImages());
            return ResponseEntity.ok(productRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
