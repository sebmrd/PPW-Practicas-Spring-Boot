package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import org.springframework.stereotype.Service;
import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product; // Importamos el modelo
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream()
                .filter(entity -> !entity.isDeleted()) // Regla de negocio: filtrar eliminados
                .map(Product::fromEntity)              // Usamos el Factory Method del modelo
                .map(Product::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        
        if (entity.isDeleted()) {
            throw new IllegalStateException("Product not found");
        }

        return Product.fromEntity(entity).toResponseDto();
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        Product product = Product.fromDto(dto); // Factory Method
        ProductEntity savedEntity = productRepository.save(product.toEntity());
        return Product.fromEntity(savedEntity).toResponseDto();
    }

    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        
        if (entity.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted product");
        }

        Product product = Product.fromEntity(entity);
        product.update(dto); // Lógica de actualización en el modelo
        ProductEntity savedEntity = productRepository.save(product.toEntity());
        return Product.fromEntity(savedEntity).toResponseDto();
    }

    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted product");
        }

        Product product = Product.fromEntity(entity);
        product.partialUpdate(dto); // Lógica de actualización parcial en el modelo
        ProductEntity savedEntity = productRepository.save(product.toEntity());
        return Product.fromEntity(savedEntity).toResponseDto();
    }

    @Override
    public void delete(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Product is already deleted");
        }

        entity.setDeleted(true);
        productRepository.save(entity);
    }
}
