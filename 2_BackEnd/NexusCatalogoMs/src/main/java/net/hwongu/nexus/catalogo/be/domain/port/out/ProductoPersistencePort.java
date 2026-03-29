package net.hwongu.nexus.catalogo.be.domain.port.out;

import net.hwongu.nexus.catalogo.be.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Define el acceso a datos de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface ProductoPersistencePort {

    List<Producto> findAllOrdered();

    Optional<Producto> findById(Integer id);

    Producto save(Producto producto);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    void flush();
}
