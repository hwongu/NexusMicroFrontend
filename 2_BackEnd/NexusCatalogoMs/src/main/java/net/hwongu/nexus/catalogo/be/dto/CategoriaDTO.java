package net.hwongu.nexus.catalogo.be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta datos de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {

    private Integer idCategoria;

    @NotBlank(message = "El nombre de la categoria es obligatorio.")
    private String nombre;

    private String descripcion;
}
