package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.util.List;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

public class ProductMapper {

    // 1. Mapeo individual (De Entity a DTO)
    public static ProductResponseDto toResponse(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Mapeo del objeto anidado Owner (Usuario)
        if (entity.getOwner() != null) {
            ProductResponseDto.UserSummaryDto ownerDto = new ProductResponseDto.UserSummaryDto();
            ownerDto.setId(entity.getOwner().getId());
            ownerDto.setName(entity.getOwner().getName());
            ownerDto.setEmail(entity.getOwner().getEmail());
            dto.setOwner(ownerDto);
        }

        // Mapeo del objeto anidado Category (Categoría)
        if (entity.getCategory() != null) {
            ProductResponseDto.CategorySummaryDto categoryDto = new ProductResponseDto.CategorySummaryDto();
            categoryDto.setId(entity.getCategory().getId());
            categoryDto.setName(entity.getCategory().getName());
            categoryDto.setDescription(entity.getCategory().getDescription());
            dto.setCategory(categoryDto);
        }

        return dto;
    }

    // 2. NUEVO: Mapeo "Tipo Lista" (El que pidió el profe)
    public static List<ProductResponseDto> toResponseList(List<ProductEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }
}
