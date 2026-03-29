package net.hwongu.nexus.catalogo.be.domain.port.out;

import net.hwongu.nexus.catalogo.be.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Define el acceso a datos de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface CategoriaPersistencePort {

    List<Categoria> findAllOrdered();

    Optional<Categoria> findById(Integer id);

    Categoria save(Categoria categoria);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    void flush();
}
