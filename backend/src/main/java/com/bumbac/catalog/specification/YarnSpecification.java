package com.bumbac.catalog.specification;

import com.bumbac.catalog.entity.Yarn;
import org.springframework.data.jpa.domain.Specification;

public class YarnSpecification {

    public static Specification<Yarn> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Yarn> hasBrand(String brand) {
        return (root, query, cb) -> cb.equal(root.get("brand"), brand);
    }

    public static Specification<Yarn> hasMaterial(String material) {
        return (root, query, cb) -> cb.equal(root.get("material"), material);
    }

    // другие фильтры по мере необходимости
}
