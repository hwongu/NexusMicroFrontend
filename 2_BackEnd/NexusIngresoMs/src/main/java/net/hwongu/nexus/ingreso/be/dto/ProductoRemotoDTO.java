package net.hwongu.nexus.ingreso.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos remotos de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRemotoDTO {

    private Integer idProducto;
    private Integer idCategoria;
    private String nombreCategoria;
    private String nombre;
    private Double precio;
    private Integer stock;
}
