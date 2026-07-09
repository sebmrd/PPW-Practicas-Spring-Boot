package ec.edu.ups.icc.fundamentos01.categories.entities;

import ec.edu.ups.icc.fundamentos01.core.entities.BaseEntity;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

/*
 * Entidad JPA del recurso categories.
 *
 * Representa la tabla categories en PostgreSQL.
 * Una categoría puede estar asociada a muchos productos,
 * pero en esta práctica la relación se define desde ProductEntity.
 */
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<ProductEntity> products = new HashSet<>();

    // Constructor vacío
    public CategoryEntity() {
    }

    // Constructor lleno
    public CategoryEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductEntity> products) {
        this.products = products;
    }
}
