package net.hwongu.nexus.ingreso.be.domain.port.out;

import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;
import net.hwongu.nexus.ingreso.be.domain.model.Ingreso;

import java.util.List;
import java.util.Optional;

/**
 * Define el acceso a datos de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface IngresoPersistencePort {

    List<Ingreso> findAllIngresosOrderByIdDesc();

    boolean existsIngresoById(Integer idIngreso);

    Optional<Ingreso> findIngresoById(Integer idIngreso);

    List<DetalleIngreso> findDetallesByIngresoId(Integer idIngreso);

    Ingreso saveIngreso(Ingreso ingreso);

    List<DetalleIngreso> saveDetalles(List<DetalleIngreso> detalles);
}
