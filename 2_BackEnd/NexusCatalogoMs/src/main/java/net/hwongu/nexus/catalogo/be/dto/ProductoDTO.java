package net.hwongu.nexus.catalogo.be.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    private Integer idProducto;

    @NotNull(message = "El id de la categoria es obligatorio.")
    @Min(value = 1, message = "El id de la categoria debe ser mayor a cero.")
    private Integer idCategoria;

    private String nombreCategoria;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    private String nombre;

    @NotNull(message = "El precio del producto es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero.")
    private Double precio;

    @NotNull(message = "El stock del producto es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;
}
