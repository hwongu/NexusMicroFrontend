package net.hwongu.nexus.ingreso.be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos para actualizar estados de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoIngresoDTO {

    @NotBlank(message = "El estado es obligatorio.")
    private String estado;
}
