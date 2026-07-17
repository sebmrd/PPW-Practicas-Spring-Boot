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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Productos", description = "Gestión de productos con paginación, roles y ownership")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos los productos", description = "Devuelve todos los productos activos sin paginación. Este endpoint es administrativo y requiere ROLE_ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado completo de productos"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene ROLE_ADMIN")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un producto por ID", description = "Devuelve un producto específico por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable("id") Long id) {
        return service.findOne(id);
    }

    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto y lo devuelve.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado completo de productos"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene ROLE_ADMIN")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.create(dto, currentUser);
    }

    @Operation(summary = "Actualizar un producto", description = "Permite actualizar un producto específico por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para actualizar este producto"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.update(id, dto, currentUser);
    }

    @Operation(summary = "Actualizar parcialmente un producto", description = "Permite actualizar parcialmente un producto específico por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para actualizar este producto"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @Operation(summary = "Eliminar un producto", description = "Permite eliminar un producto específico por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar este producto"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
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
    @Operation(summary = "Validar nombre de producto", description = "Verifica si un nombre de producto ya existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuesta de validación"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
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
    @Operation(summary = "Listar productos paginados", description = "Devuelve una página de productos activos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de productos paginado"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
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
    @Operation(summary = "Listar productos slice", description = "Devuelve un slice de productos activos del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de productos slice"),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
    })
    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @Valid PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser // Inyectamos al usuario
    ) {
        Slice<ProductResponseDto> products = service.findAllSlice(pagination, currentUser);
        return ResponseEntity.ok(products);
    }
}
