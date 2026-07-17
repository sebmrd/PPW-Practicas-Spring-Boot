package ec.edu.ups.icc.fundamentos01.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parámetros para paginación y ordenamiento")
public class PaginationDto {

    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    @Schema(description = "Número de página, iniciando en 0", example = "0")
    private int page = 0;

    @Min(value = 1, message = "El tamaño debe ser mayor o igual a 1")
    @Max(value = 100, message = "El tamaño no debe superar 100 registros")
    @Schema(description = "Cantidad de registros por página", example = "5")
    private int size = 10;

    @Schema(description = "Campo por el cual se ordenan los resultados", example = "price")
    private String sortBy = "id";

    @Schema(description = "Dirección de ordenamiento", example = "desc")
    private String direction = "asc";

    public PaginationDto() {}

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
