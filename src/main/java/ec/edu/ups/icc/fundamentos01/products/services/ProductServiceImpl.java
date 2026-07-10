package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
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
        return ProductMapper.toResponse(entity); 
    }

    /*
     * Elimina lógicamente un producto.
     * Se valida ownership en el servicio.
     */
    @Override
    @Transactional
    public void delete(Long id, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);
        
        // VALIDA QUE EL USUARIO AUTENTICADO PUEDA ELIMINAR ESTE PRODUCTO
        validateOwnership(entity, currentUser);
        
        entity.setDeleted(true);
        productRepository.save(entity);
    }

    /*
     * Crea un producto usando como owner al usuario autenticado.
     */
    @Override
    @Transactional
    public ProductResponseDto create(CreateProductDto dto, UserDetailsImpl currentUser) {
        // 1. Obtenemos el owner desde el token JWT, NO desde el DTO
        UserEntity owner = findCurrentUserEntity(currentUser);

        // 2. Encontramos la categoria
        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        // Validacion de negocio, por ejemplo que no exista un producto con el mismo nombre
        if (productRepository.findByNameIgnoreCaseAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }

        // Generamos la entidad a partir del DTO
        ProductEntity entity = new ProductEntity();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setOwner(owner); // Se asigna automáticamente al usuario logueado
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);
        return ProductMapper.toResponse(savedEntity);
    }

    /*
     * Actualiza completamente un producto activo.
     * Se valida ownership en el servicio.
     */
    @Override
    @Transactional
    public ProductResponseDto update(Long id, UpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        // VALIDA QUE EL USUARIO AUTENTICADO PUEDA MODIFICAR ESTE PRODUCTO
        validateOwnership(entity, currentUser);

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
     * Se valida ownership en el servicio.
     */
    @Override
    @Transactional
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);

        // VALIDA QUE EL USUARIO AUTENTICADO PUEDA MODIFICAR ESTE PRODUCTO
        validateOwnership(entity, currentUser);

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

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination) {
        Pageable pageable = createPageable(pagination);
        return productRepository.findActivePage(pageable)
                .map(ProductMapper::toResponse);
    }

    // Reemplaza tu método findAllSlice actual por este:
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination, UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        
        Pageable pageable = createPageable(pagination);
        
        // Aquí está la magia: filtramos directamente en la base de datos por el ID del owner
        return productRepository.findByOwner_IdAndDeletedFalse(currentUser.getId(), pageable)
                .map(ProductMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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


    // ==========================================
    // --- Helpers Privados Generales ---
    // ==========================================

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


    // ==========================================
    // --- Helpers Privados Paginación ---
    // ==========================================

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


    // ==========================================
    // --- Helpers Privados de Seguridad / Ownership ---
    // ==========================================

    private ProductEntity findActiveProductOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private UserEntity findCurrentUserEntity(UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autorizado o eliminado"));
    }

    private void validateOwnership(ProductEntity product, UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        // ADMIN puede modificar cualquier producto
        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }
        if (product.getOwner() == null || product.getOwner().getId() == null) {
            throw new AccessDeniedException("El producto no tiene propietario válido");
        }
        // USER solo puede modificar sus propios productos
        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No puedes modificar productos ajenos");
        }
    }

    private boolean hasRole(UserDetailsImpl user, String role) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }
}
