package net.hwongu.nexus.catalogo.be.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el dominio de Categoria
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
public class Categoria {

    private Integer idCategoria;
    private String nombre;
    private String descripcion;
}
