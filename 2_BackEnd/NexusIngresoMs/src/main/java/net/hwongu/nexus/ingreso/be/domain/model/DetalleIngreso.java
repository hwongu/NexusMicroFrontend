package net.hwongu.nexus.ingreso.be.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el dominio de DetalleIngreso
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
public class DetalleIngreso {

    private Integer idDetalle;
    private Integer idIngreso;
    private Integer idProducto;
    private Integer cantidad;
    private Double precioCompra;
}
