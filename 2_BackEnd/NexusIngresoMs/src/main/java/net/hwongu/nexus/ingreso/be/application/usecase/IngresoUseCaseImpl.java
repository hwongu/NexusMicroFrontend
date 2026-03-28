package net.hwongu.nexus.ingreso.be.application.usecase;

import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;
import net.hwongu.nexus.ingreso.be.domain.model.Ingreso;
import net.hwongu.nexus.ingreso.be.domain.port.in.IngresoUseCase;
import net.hwongu.nexus.ingreso.be.domain.port.out.IngresoPersistencePort;
import net.hwongu.nexus.ingreso.be.domain.port.out.ProductoRemotoPort;
import net.hwongu.nexus.ingreso.be.domain.port.out.StockCatalogoPort;
import net.hwongu.nexus.ingreso.be.domain.port.out.UsuarioRemotoPort;
import net.hwongu.nexus.ingreso.be.dto.DetalleIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.IngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.ProductoRemotoDTO;
import net.hwongu.nexus.ingreso.be.dto.RegistrarIngresoRequestDTO;
import net.hwongu.nexus.ingreso.be.dto.UsuarioRemotoDTO;
import net.hwongu.nexus.ingreso.be.exception.BadRequestException;
import net.hwongu.nexus.ingreso.be.exception.IntegracionRemotaException;
import net.hwongu.nexus.ingreso.be.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Coordina los casos de uso de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Service
public class IngresoUseCaseImpl implements IngresoUseCase {

    private static final String OPERACION_SUMAR = "SUMAR";
    private static final String OPERACION_RESTAR = "RESTAR";
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_RECIBIDO = "RECIBIDO";
    private static final String ESTADO_ANULADO = "ANULADO";
    private static final String ESTADO_ERROR_INTEGRACION = "ERROR_INTEGRACION";

    private final IngresoPersistencePort ingresoPersistencePort;
    private final UsuarioRemotoPort usuarioRemotoPort;
    private final ProductoRemotoPort productoRemotoPort;
    private final StockCatalogoPort stockCatalogoPort;

    public IngresoUseCaseImpl(IngresoPersistencePort ingresoPersistencePort,
                              UsuarioRemotoPort usuarioRemotoPort,
                              ProductoRemotoPort productoRemotoPort,
                              StockCatalogoPort stockCatalogoPort) {
        this.ingresoPersistencePort = ingresoPersistencePort;
        this.usuarioRemotoPort = usuarioRemotoPort;
        this.productoRemotoPort = productoRemotoPort;
        this.stockCatalogoPort = stockCatalogoPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoDTO> listarIngresos() {
        return ingresoPersistencePort.findAllIngresosOrderByIdDesc()
                .stream()
                .map(this::convertirIngresoADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleIngresoDTO> buscarDetallesPorIngreso(Integer idIngreso) {
        if (!ingresoPersistencePort.existsIngresoById(idIngreso)) {
            throw new RecursoNoEncontradoException("Ingreso no encontrado.");
        }

        return ingresoPersistencePort.findDetallesByIngresoId(idIngreso)
                .stream()
                .map(this::convertirDetalleADTO)
                .toList();
    }

    @Override
    @Transactional(noRollbackFor = IntegracionRemotaException.class)
    public IngresoDTO registrarIngresoCompleto(RegistrarIngresoRequestDTO requestDTO) {
        IngresoDTO ingresoDTO = requestDTO.getIngreso();
        List<DetalleIngresoDTO> detalleDTOs = requestDTO.getDetalles();

        UsuarioRemotoDTO usuario = usuarioRemotoPort.validarUsuarioActivo(ingresoDTO.getIdUsuario());
        detalleDTOs.forEach(detalleDTO -> productoRemotoPort.validarProductoExistente(detalleDTO.getIdProducto()));

        Ingreso ingreso = Ingreso.builder()
                .idUsuario(usuario.getIdUsuario())
                .fechaIngreso(ingresoDTO.getFechaIngreso() != null ? ingresoDTO.getFechaIngreso() : LocalDateTime.now())
                .estado(ESTADO_PENDIENTE)
                .build();

        Ingreso ingresoGuardado = ingresoPersistencePort.saveIngreso(ingreso);

        List<DetalleIngreso> detalles = detalleDTOs.stream()
                .map(detalleDTO -> convertirDetalleADominio(detalleDTO, ingresoGuardado.getIdIngreso()))
                .toList();

        List<DetalleIngreso> detallesGuardados = ingresoPersistencePort.saveDetalles(detalles);

        try {
            stockCatalogoPort.actualizarStock(detallesGuardados, OPERACION_SUMAR);
            ingresoGuardado.setEstado(ESTADO_RECIBIDO);
            ingresoPersistencePort.saveIngreso(ingresoGuardado);
        } catch (RestClientException exception) {
            ingresoGuardado.setEstado(ESTADO_ERROR_INTEGRACION);
            ingresoPersistencePort.saveIngreso(ingresoGuardado);
            throw new IntegracionRemotaException(
                    "El ingreso fue registrado localmente, pero no se pudo actualizar el stock en catalogo. "
                            + "El ingreso quedo en estado ERROR_INTEGRACION."
            );
        }

        return construirIngresoDTO(ingresoGuardado, usuario.getUsername());
    }

    @Override
    @Transactional(noRollbackFor = IntegracionRemotaException.class)
    public void anularIngreso(Integer idIngreso) {
        Ingreso ingreso = ingresoPersistencePort.findIngresoById(idIngreso)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ingreso no encontrado."));

        if (ESTADO_ANULADO.equalsIgnoreCase(ingreso.getEstado())) {
            throw new BadRequestException("El ingreso ya se encuentra anulado.");
        }

        if (!ESTADO_RECIBIDO.equalsIgnoreCase(ingreso.getEstado())) {
            throw new BadRequestException("Solo se puede anular un ingreso en estado RECIBIDO.");
        }

        List<DetalleIngreso> detalles = ingresoPersistencePort.findDetallesByIngresoId(idIngreso);

        try {
            stockCatalogoPort.actualizarStock(detalles, OPERACION_RESTAR);
            ingreso.setEstado(ESTADO_ANULADO);
            ingresoPersistencePort.saveIngreso(ingreso);
        } catch (RestClientException exception) {
            ingreso.setEstado(ESTADO_ERROR_INTEGRACION);
            ingresoPersistencePort.saveIngreso(ingreso);
            throw new IntegracionRemotaException(
                    "No se pudo revertir el stock en catalogo. El ingreso quedo en estado ERROR_INTEGRACION."
            );
        }
    }

    @Override
    @Transactional
    public void actualizarEstadoIngreso(Integer idIngreso, String nuevoEstado) {
        Ingreso ingreso = ingresoPersistencePort.findIngresoById(idIngreso)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ingreso no encontrado."));

        ingreso.setEstado(nuevoEstado.trim().toUpperCase());
        ingresoPersistencePort.saveIngreso(ingreso);
    }

    private IngresoDTO convertirIngresoADTO(Ingreso ingreso) {
        UsuarioRemotoDTO usuario = usuarioRemotoPort.buscarUsuario(ingreso.getIdUsuario());
        return construirIngresoDTO(ingreso, usuario.getUsername());
    }

    private DetalleIngresoDTO convertirDetalleADTO(DetalleIngreso detalle) {
        ProductoRemotoDTO producto = productoRemotoPort.buscarProducto(detalle.getIdProducto());

        return DetalleIngresoDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .idIngreso(detalle.getIdIngreso())
                .idProducto(detalle.getIdProducto())
                .nombreProducto(producto.getNombre())
                .cantidad(detalle.getCantidad())
                .precioCompra(detalle.getPrecioCompra())
                .build();
    }

    private IngresoDTO construirIngresoDTO(Ingreso ingreso, String username) {
        return IngresoDTO.builder()
                .idIngreso(ingreso.getIdIngreso())
                .idUsuario(ingreso.getIdUsuario())
                .username(username)
                .fechaIngreso(ingreso.getFechaIngreso())
                .estado(ingreso.getEstado())
                .build();
    }

    private DetalleIngreso convertirDetalleADominio(DetalleIngresoDTO detalleDTO, Integer idIngreso) {
        return DetalleIngreso.builder()
                .idIngreso(idIngreso)
                .idProducto(detalleDTO.getIdProducto())
                .cantidad(detalleDTO.getCantidad())
                .precioCompra(detalleDTO.getPrecioCompra())
                .build();
    }
}
