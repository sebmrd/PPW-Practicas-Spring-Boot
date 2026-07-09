package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import ec.edu.ups.icc.fundamentos01.core.dto.PaginationDto;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable("id") Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(@Valid @RequestBody CreateProductDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(@Valid @PathVariable("id") Long id, @RequestBody UpdateProductDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(@Valid @PathVariable("id") Long id, @RequestBody PartialUpdateProductDto dto) {
        return service.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
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

    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(@Valid @ModelAttribute PaginationDto pagination) {
        return service.findAllPage(pagination);
    }

    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(@Valid @ModelAttribute PaginationDto pagination) {
        return service.findAllSlice(pagination);
    }
}
