package ec.edu.ups.icc.fundamentos01.products.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * DTO utilizado para actualizar completamente un producto.
 *
 * Permite actualizar los datos editables del producto
 * y cambiar la categoría asociada.
 *
 * No permite cambiar el usuario propietario.
 */
public class UpdateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private java.util.Set<Long> categoryIds;

    // Constructor vacío
    public UpdateProductDto() {
    }

    // Constructor lleno
    public UpdateProductDto(String name, Double price, Integer stock, Long categoryId) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryIds = new java.util.HashSet<>();
        this.categoryIds.add(categoryId);
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public java.util.Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(java.util.Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
