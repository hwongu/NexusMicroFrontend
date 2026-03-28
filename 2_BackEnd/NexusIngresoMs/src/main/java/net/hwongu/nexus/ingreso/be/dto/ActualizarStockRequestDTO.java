package net.hwongu.nexus.ingreso.be.dto;

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

    private Integer cantidad;
    private String operacion;
}
