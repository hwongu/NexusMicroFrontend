package net.hwongu.nexus.catalogo.be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos para actualizar stock
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarStockRequestDTO {

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero.")
    private Integer cantidad;

    @NotBlank(message = "La operacion es obligatoria.")
    private String operacion;
}
