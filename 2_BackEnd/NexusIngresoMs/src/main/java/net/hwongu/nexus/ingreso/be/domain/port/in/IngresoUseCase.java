package net.hwongu.nexus.ingreso.be.domain.port.in;

import net.hwongu.nexus.ingreso.be.dto.DetalleIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.IngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.RegistrarIngresoRequestDTO;

import java.util.List;

/**
 * Define los casos de uso de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface IngresoUseCase {

    List<IngresoDTO> listarIngresos();

    List<DetalleIngresoDTO> buscarDetallesPorIngreso(Integer idIngreso);

    IngresoDTO registrarIngresoCompleto(RegistrarIngresoRequestDTO requestDTO);

    void anularIngreso(Integer idIngreso);

    void actualizarEstadoIngreso(Integer idIngreso, String nuevoEstado);
}
