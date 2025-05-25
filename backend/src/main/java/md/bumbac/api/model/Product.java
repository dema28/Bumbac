package md.bumbac.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String sku;

    private String colorName;
    private String hexColor;

    private String unitType;
    private String availabilityStatus;

    private BigDecimal price;

    @ElementCollection
    private List<String> galleryImages;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    @ElementCollection
    @CollectionTable(
            name = "product_tech_specs",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private Map<String, String> technicalDetails;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /** логическое удаление */
    private boolean deleted = false;

    /** количество на складе */
    private int stockQuantity;

    /** язык записи: "ru", "ro", "en" */
    private String lang;

    // ↓ вставьте внутри класса Product, сразу после поля stockQuantity
    public int getStock() {
        return this.stockQuantity;
    }

    public void setStock(int qty) {
        this.stockQuantity = qty;
    }

}// ← теперь последняя скобка – и после неё ничего нет
