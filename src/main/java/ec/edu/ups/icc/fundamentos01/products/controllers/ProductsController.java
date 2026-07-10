package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable("id") Long id) {
        return service.findOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.create(dto, currentUser);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.update(id, dto, currentUser);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        service.delete(id, currentUser);
    }

    /*
     * Endpoint custom para validar si el nombre de un producto ya existe.
     *
     * POST /products/validate-name
     */
    @PostMapping("/validate-name")
    public java.util.Map<String, Boolean> validateName(@RequestBody java.util.Map<String, String> request) {
        // Extraemos el nombre del JSON que enviaste desde Bruno
        String nameToCheck = request.get("name");
        
        // Llamamos al servicio para ver si existe
        boolean exists = service.validateName(nameToCheck);
        
        // Devolvemos un JSON estructurado de respuesta
        return java.util.Map.of("exists", exists);
    }

    /**
     * Paginación normal: TODOS pueden ver TODOS los productos activos.
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductResponseDto>> findAllPaginado(
            @Valid PaginationDto pagination
    ) {
        Page<ProductResponseDto> products = service.findAllPage(pagination);
        return ResponseEntity.ok(products);
    }

    /**
     * Slice: TODOS pueden acceder, pero SOLO ven SUS propios productos.
     */
    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @Valid PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser // Inyectamos al usuario
    ) {
        Slice<ProductResponseDto> products = service.findAllSlice(pagination, currentUser);
        return ResponseEntity.ok(products);
    }
}
