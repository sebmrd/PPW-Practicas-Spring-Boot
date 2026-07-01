package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

/*
 * DTO utilizado para devolver al cliente los datos públicos
 * de un producto, incluyendo información resumida
 * del usuario propietario y de la categoría.
 */
public class ProductResponseDto {

    private Long id;

    private String name;

    private Double price;

    private Integer stock;

    private UserSummaryDto owner; 
    private CategorySummaryDto category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    // Constructor vacío
    public ProductResponseDto() {
    }

    // Constructor lleno
    public ProductResponseDto(Long id, String name, Double price, Integer stock, UserSummaryDto owner, CategorySummaryDto category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.owner = owner;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public UserSummaryDto getOwner() {
        return owner;
    }

    public CategorySummaryDto getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setOwner(UserSummaryDto owner) {
        this.owner = owner;
    }

    public void setCategory(CategorySummaryDto category) {
        this.category = category;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Clase interna para representar un resumen del usuario
    public static class UserSummary {
        private Long id;
        private String email;
        private String name;

        public UserSummary(Long id, String email, String name) {
            this.id = id;
            this.email = email;
            this.name = name;
        }

        // Getters y setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class UserSummaryDto {
        private Long id;
        private String name;
        private String email;
        // Necesitas sus constructores, getters y setters aquí
        public UserSummaryDto() {
        }

        public UserSummaryDto(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class CategorySummaryDto {
        private Long id;
        private String name;
        private String description;
        // Necesitas sus constructores, getters y setters aquí

        public CategorySummaryDto() {
        }

        public CategorySummaryDto(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

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
    }
}
