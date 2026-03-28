package net.hwongu.nexus.ingreso.be.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos de detalle de ingreso
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleIngresoDTO {

    private Integer idDetalle;

    private Integer idIngreso;

    @NotNull(message = "El id del producto es obligatorio.")
    @Min(value = 1, message = "El id del producto debe ser mayor a cero.")
    private Integer idProducto;

    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero.")
    private Integer cantidad;

    @NotNull(message = "El precio de compra es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a cero.")
    private Double precioCompra;
}
