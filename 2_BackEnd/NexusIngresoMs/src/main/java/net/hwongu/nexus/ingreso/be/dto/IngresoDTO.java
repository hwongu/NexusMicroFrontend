package net.hwongu.nexus.ingreso.be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Transporta datos de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngresoDTO {

    private Integer idIngreso;

    @NotNull(message = "El id del usuario es obligatorio.")
    @Min(value = 1, message = "El id del usuario debe ser mayor a cero.")
    private Integer idUsuario;

    private String username;

    private LocalDateTime fechaIngreso;

    private String estado;
}
