package net.hwongu.nexus.catalogo.be.domain.port.in;

import net.hwongu.nexus.catalogo.be.domain.model.Categoria;

import java.util.List;

/**
 * Define los casos de uso de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface CategoriaUseCase {

    List<Categoria> listarCategorias();

    Categoria buscarCategoriaPorId(Integer id);

    Categoria registrarCategoria(Categoria categoria);

    void actualizarCategoria(Integer id, Categoria categoria);

    void eliminarCategoria(Integer id);
}
