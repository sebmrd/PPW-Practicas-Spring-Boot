package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Repositorio encargado de gestionar la persistencia
 * de productos usando Spring Data JPA.
 *
 * Incluye consultas usando relaciones con UserEntity y CategoryEntity.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByNameIgnoreCaseAndDeletedFalse(String name);

    List<ProductEntity> findByDeletedFalse();

    Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

    List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

    List<ProductEntity> findByCategory_IdAndDeletedFalse(Long categoryId);

    List<ProductEntity> findByCategory_NameIgnoreCaseAndDeletedFalse(String categoryName);

    @Query("""
        SELECT p FROM ProductEntity p 
        WHERE p.deleted = false 
        AND p.owner.id = :userId 
        AND p.owner.deleted = false 
        AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) 
        AND (:minPrice IS NULL OR p.price >= :minPrice) 
        AND (:maxPrice IS NULL OR p.price <= :maxPrice) 
        AND (:categoryId IS NULL OR p.category.id = :categoryId) 
        AND (:categoryId IS NULL OR p.category.deleted = false)
    """)
    List<ProductEntity> findByOwnerIdWithFilters(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId
    );

    @Query("""
        SELECT p FROM ProductEntity p 
        WHERE p.deleted = false 
        AND p.category.id = :categoryId 
        AND p.category.deleted = false 
        AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) 
        AND (:minPrice IS NULL OR p.price >= :minPrice) 
        AND (:maxPrice IS NULL OR p.price <= :maxPrice) 
        AND (:userId IS NULL OR p.owner.id = :userId) 
        AND (:userId IS NULL OR p.owner.deleted = false)
    """)
    List<ProductEntity> findByCategoryIdWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId
    );
}
