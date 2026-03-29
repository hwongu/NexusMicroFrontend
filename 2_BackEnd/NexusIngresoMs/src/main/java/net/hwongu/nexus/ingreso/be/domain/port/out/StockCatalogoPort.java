package net.hwongu.nexus.ingreso.be.domain.port.out;

import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;

import java.util.List;

/**
 * Define la actualizacion remota de stock
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface StockCatalogoPort {

    void actualizarStock(List<DetalleIngreso> detalles, String operacion);
}
