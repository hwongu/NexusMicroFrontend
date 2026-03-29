package net.hwongu.nexus.catalogo.be.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el dominio de Producto
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
public class Producto {

    private Integer idProducto;
    private Categoria categoria;
    private String nombre;
    private Double precio;
    private Integer stock;
}
