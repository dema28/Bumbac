package md.bumbac.api.service;

import lombok.RequiredArgsConstructor;
import md.bumbac.api.model.Product;
import md.bumbac.api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /* ---------- CRUD ---------- */

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /* ---------- фильтрация ---------- */

    public List<Product> getByCategory(String slug) {
        return productRepository.findByCategory_Slug(slug);
    }

    public List<Product> filterByColor(String color) {
        return productRepository.findByColorNameContainingIgnoreCase(color);
    }

    public List<Product> filterByPrice(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public List<Product> getByLang(String lang) {
        return productRepository.findByLang(lang);
    }
}   // ← финальная скобка, больше ничего вне класса
