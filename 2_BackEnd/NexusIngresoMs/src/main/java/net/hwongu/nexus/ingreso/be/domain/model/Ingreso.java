package net.hwongu.nexus.ingreso.be.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa el dominio de Ingreso
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingreso {

    private Integer idIngreso;
    private Integer idUsuario;
    private LocalDateTime fechaIngreso;
    private String estado;
}
