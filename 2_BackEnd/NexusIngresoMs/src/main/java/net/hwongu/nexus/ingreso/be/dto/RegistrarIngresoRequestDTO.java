package net.hwongu.nexus.ingreso.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Transporta datos para registrar ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarIngresoRequestDTO {

    @Valid
    @NotNull(message = "La cabecera del ingreso es obligatoria.")
    private IngresoDTO ingreso;

    @Valid
    @NotEmpty(message = "El ingreso debe tener al menos un detalle.")
    private List<DetalleIngresoDTO> detalles;
}
