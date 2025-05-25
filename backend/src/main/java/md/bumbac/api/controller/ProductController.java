package md.bumbac.api.controller;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Product;
import md.bumbac.api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{slug}")
    public List<Product> getByCategory(@PathVariable String slug) {
        return productService.getByCategory(slug);
    }

    @GetMapping("/filter")
    public List<Product> filter(@RequestParam(required = false) String color,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice) {
        if (color != null) return productService.filterByColor(color);
        if (minPrice != null && maxPrice != null) return productService.filterByPrice(minPrice, maxPrice);
        return productService.getAll();
    }

    // ---------- добавляем метод внутри класса ----------
    @GetMapping("/lang")
    public List<Product> getByLanguage(@RequestParam(defaultValue = "ru") String lang) {
        return productService.getByLang(lang);
    }
}   // ← это теперь единственная финальная скобка
