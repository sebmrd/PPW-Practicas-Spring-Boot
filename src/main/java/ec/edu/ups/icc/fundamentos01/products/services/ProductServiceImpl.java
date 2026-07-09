package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repository.UserRepository;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }
        
        List<ProductEntity> list = productRepository.findByOwner_IdAndDeletedFalse(userId);

        return ProductMapper.toResponseList(list);
    }

    @Override
    public List<ProductResponseDto> findAll() {
        List<ProductEntity> list = productRepository.findByDeletedFalse();
        return ProductMapper.toResponseList(list);
    }

    @Override
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return ProductMapper.toResponse(entity); // O usando tu ProductMapper
    }

    @Override
    public void delete(Long id) {
        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        
        entity.setDeleted(true);
        productRepository.save(entity);
    }

    /*
    * Crea un producto asociado a un usuario y a una categoría.
    *
    * Valida:
    * - que el usuario exista
    * - que la categoría exista
    * - que no exista un producto activo con el mismo nombre
    */
    @Override
    public ProductResponseDto create(CreateProductDto dto) {

        // 1 Encontramos el user
        UserEntity owner = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (owner.isDeleted()) {
            throw new NotFoundException("User not found");
        }

        // 2 Encontramos la categoria
        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        // validadacion de negocio, por ejemplo que no exista un producto  con el mismo nombre
        if (productRepository.findByNameIgnoreCaseAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }


        // Genereamos la entidad a partir del DTO

        ProductEntity entity = new ProductEntity();

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setOwner(owner);
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(savedEntity);
    }

    /*
    * Actualiza completamente un producto activo.
    *
    * No permite cambiar el usuario propietario.
    * Sí permite cambiar la categoría.
    */
    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(savedEntity);
    }

    /*
    * Actualiza parcialmente un producto activo.
    *
    * Solo modifica los campos enviados en el DTO.
    */
    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {

        ProductEntity entity = productRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }

        if (dto.getCategoryIds() != null) {
            Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());
            entity.setCategories(categories);
        }

        ProductEntity savedEntity = productRepository.save(entity);

        return ProductMapper.toResponse(savedEntity);
    }

    /*
     * Valida si un nombre de producto ya existe en la base de datos.
     * Devuelve true si ya existe, false si está disponible.
     */
    @Override
    public boolean validateName(String name) {
        return productRepository.findByNameIgnoreCaseAndDeletedFalse(name).isPresent();
    }

    @Override
    public List<ProductResponseDto> findByUserIdWithFilters(Long userId, ProductFilterByUserDto filters) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }
        
        validateUserFilters(filters);
        String name = normalizeName(filters.getName());
        
        List<ProductEntity> list = productRepository.findByOwnerIdWithFilters(
                userId, name, filters.getMinPrice(), filters.getMaxPrice()
        );
        
        return ProductMapper.toResponseList(list);
    }

    @Override
    public List<ProductResponseDto> findByCategoryIdWithFilters(Long categoryId, ProductFilterByCategoryDto filters) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }
        
        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());
        
        List<ProductEntity> list = productRepository.findByCategoryIdWithFilters(
                categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId()
        );
        
        return ProductMapper.toResponseList(list);
    }

    // --- Helpers Privados ---
    private void validateUserFilters(ProductFilterByUserDto filters) {
        if (filters == null) return;
        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al mínimo");
        };
    }

    private void validateCategoryFilters(ProductFilterByCategoryDto filters) {
        if (filters == null) return;
        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al mínimo");
        }
        if (filters.getUserId() != null && !userRepository.existsByIdAndDeletedFalse(filters.getUserId())) {
            throw new NotFoundException("User not found");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return name.trim();
    }

    /*
     * Valida que todas las categorías existan y estén activas.
     * Retorna el conjunto de entidades CategoryEntity que se asociarán al producto.
     */
    private java.util.Set<CategoryEntity> validateAndGetCategories(java.util.Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos una categoría");
        }
        
        java.util.Set<CategoryEntity> categories = new java.util.HashSet<>();
        for (Long catId : categoryIds) {
            CategoryEntity category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            if (category.isDeleted()) {
                throw new NotFoundException("Category not found");
            }
            categories.add(category);
        }
        return categories;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {
        Pageable pageable = createPageable(pagination);
        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toResponse); // Usamos tu mapper directo
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination) {
        Pageable pageable = createPageable(pagination);
        return productRepository.findActiveSlice(pageable)
                .map(ProductMapper::toResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<ProductResponseDto> findByCategoryIdWithFiltersPage(Long categoryId, ProductFilterByCategoryDto filters, PaginationDto pagination) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }
        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);
        
        return productRepository.findByCategoryIdWithFiltersPage(
                categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId(), pageable
        ).map(ProductMapper::toResponse);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(Long categoryId, ProductFilterByCategoryDto filters, PaginationDto pagination) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }
        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersSlice(
                categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId(), pageable
        ).map(ProductMapper::toResponse);
    }

    // --- Helpers de Paginación ---
    private Pageable createPageable(PaginationDto pagination) {
        String sortBy = normalizeSortBy(pagination.getSortBy());
        Sort.Direction direction = normalizeDirection(pagination.getDirection());
        Sort sort = Sort.by(direction, sortBy);
        return org.springframework.data.domain.PageRequest.of(pagination.getPage(), pagination.getSize(), sort);
    }

    private String normalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) return "id";
        java.util.Set<String> allowedFields = java.util.Set.of("id", "name", "price", "stock", "createdAt", "updatedAt");
        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException("Campo de ordenamiento no permitido: " + sortBy);
        }
        return sortBy;
    }

    private Sort.Direction normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) return Sort.Direction.ASC;
        if (direction.equalsIgnoreCase("asc")) return Sort.Direction.ASC;
        if (direction.equalsIgnoreCase("desc")) return Sort.Direction.DESC;
        throw new BadRequestException("Dirección de ordenamiento no válida: " + direction);
    }
}
