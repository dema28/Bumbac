package com.bumbac.modules.catalog.specification;

import com.bumbac.modules.catalog.entity.Yarn;
import org.springframework.data.jpa.domain.Specification;

public class YarnSpecification {

    public static Specification<Yarn> filterBy(String category, String brand, String material) {
        return Specification
                .where(hasCategory(category))
                .and(hasBrand(brand))
                .and(hasMaterial(material));
    }

    public static Specification<Yarn> hasCategory(String category) {
        if (category == null || category.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("name"), category);
    }

    public static Specification<Yarn> hasBrand(String brand) {
        if (brand == null || brand.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("brand").get("name"), brand);
    }

    public static Specification<Yarn> hasMaterial(String material) {
        if (material == null || material.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("material"), material);
    }
}
