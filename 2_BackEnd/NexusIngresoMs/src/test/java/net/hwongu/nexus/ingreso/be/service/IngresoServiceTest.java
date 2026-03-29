package net.hwongu.nexus.ingreso.be.service;

import net.hwongu.nexus.ingreso.be.application.usecase.IngresoUseCaseImpl;
import net.hwongu.nexus.ingreso.be.domain.model.DetalleIngreso;
import net.hwongu.nexus.ingreso.be.domain.model.Ingreso;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifica los casos de uso de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class IngresoUseCaseImplTest {

    private static final LocalDateTime FECHA_FIJA = LocalDateTime.of(2026, 3, 27, 10, 30);

    @Mock
    private IngresoPersistencePort ingresoPersistencePort;

    @Mock
    private UsuarioRemotoPort usuarioRemotoPort;

    @Mock
    private ProductoRemotoPort productoRemotoPort;

    @Mock
    private StockCatalogoPort stockCatalogoPort;

    private IngresoUseCaseImpl ingresoUseCase;

    @BeforeEach
    void setUp() {
        ingresoUseCase = new IngresoUseCaseImpl(
                ingresoPersistencePort,
                usuarioRemotoPort,
                productoRemotoPort,
                stockCatalogoPort
        );
    }

    @Test
    void listarIngresos_debeRetornarListaEnriquecidaCuandoSeguridadResponde() {
        Ingreso ingreso = crearIngreso(10, 7, FECHA_FIJA, "RECIBIDO");
        when(ingresoPersistencePort.findAllIngresosOrderByIdDesc()).thenReturn(List.of(ingreso));
        when(usuarioRemotoPort.buscarUsuario(7)).thenReturn(crearUsuarioRemoto(7, "hwongu", true));

        List<IngresoDTO> resultado = ingresoUseCase.listarIngresos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getIdIngreso()).isEqualTo(10);
        assertThat(resultado.getFirst().getIdUsuario()).isEqualTo(7);
        assertThat(resultado.getFirst().getUsername()).isEqualTo("hwongu");
        assertThat(resultado.getFirst().getFechaIngreso()).isEqualTo(FECHA_FIJA);
        assertThat(resultado.getFirst().getEstado()).isEqualTo("RECIBIDO");
    }

    @Test
    void listarIngresos_debePropagarFalloCuandoSeguridadNoResponde() {
        Ingreso ingreso = crearIngreso(10, 7, FECHA_FIJA, "RECIBIDO");
        when(ingresoPersistencePort.findAllIngresosOrderByIdDesc()).thenReturn(List.of(ingreso));
        when(usuarioRemotoPort.buscarUsuario(7))
                .thenThrow(new IntegracionRemotaException("No se pudo consultar NexusSeguridadMs. El servicio de seguridad esta caido o no disponible."));

        assertThatThrownBy(() -> ingresoUseCase.listarIngresos())
                .isInstanceOf(IntegracionRemotaException.class)
                .hasMessage("No se pudo consultar NexusSeguridadMs. El servicio de seguridad esta caido o no disponible.");
    }

    @Test
    void buscarDetallesPorIngreso_debeRetornarDetallesEnriquecidosCuandoCatalogoResponde() {
        Integer idIngreso = 20;
        DetalleIngreso detalle = crearDetalle(30, idIngreso, 5, 4, 19.5);

        when(ingresoPersistencePort.existsIngresoById(idIngreso)).thenReturn(true);
        when(ingresoPersistencePort.findDetallesByIngresoId(idIngreso)).thenReturn(List.of(detalle));
        when(productoRemotoPort.buscarProducto(5)).thenReturn(crearProductoRemoto(5, "Monitor"));

        List<DetalleIngresoDTO> resultado = ingresoUseCase.buscarDetallesPorIngreso(idIngreso);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getIdDetalle()).isEqualTo(30);
        assertThat(resultado.getFirst().getIdIngreso()).isEqualTo(idIngreso);
        assertThat(resultado.getFirst().getIdProducto()).isEqualTo(5);
        assertThat(resultado.getFirst().getNombreProducto()).isEqualTo("Monitor");
        assertThat(resultado.getFirst().getCantidad()).isEqualTo(4);
        assertThat(resultado.getFirst().getPrecioCompra()).isEqualTo(19.5);
    }

    @Test
    void buscarDetallesPorIngreso_debeLanzarExcepcionCuandoIngresoNoExiste() {
        when(ingresoPersistencePort.existsIngresoById(20)).thenReturn(false);

        assertThatThrownBy(() -> ingresoUseCase.buscarDetallesPorIngreso(20))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Ingreso no encontrado.");

        verify(ingresoPersistencePort, never()).findDetallesByIngresoId(any());
    }

    @Test
    void buscarDetallesPorIngreso_debePropagarFalloCuandoCatalogoNoResponde() {
        Integer idIngreso = 20;
        DetalleIngreso detalle = crearDetalle(30, idIngreso, 5, 4, 19.5);

        when(ingresoPersistencePort.existsIngresoById(idIngreso)).thenReturn(true);
        when(ingresoPersistencePort.findDetallesByIngresoId(idIngreso)).thenReturn(List.of(detalle));
        when(productoRemotoPort.buscarProducto(5))
                .thenThrow(new IntegracionRemotaException("No se pudo consultar NexusCatalogoMs. El servicio de catalogo esta caido o no disponible."));

        assertThatThrownBy(() -> ingresoUseCase.buscarDetallesPorIngreso(idIngreso))
                .isInstanceOf(IntegracionRemotaException.class)
                .hasMessage("No se pudo consultar NexusCatalogoMs. El servicio de catalogo esta caido o no disponible.");
    }

    @Test
    void registrarIngresoCompleto_debeGuardarYActualizarStockCuandoTodoEsValido() {
        RegistrarIngresoRequestDTO request = crearSolicitudRegistro(7, 5, 4, 19.5);
        UsuarioRemotoDTO usuario = crearUsuarioRemoto(7, "hwongu", true);
        ProductoRemotoDTO producto = crearProductoRemoto(5, "Monitor");

        when(usuarioRemotoPort.validarUsuarioActivo(7)).thenReturn(usuario);
        when(productoRemotoPort.validarProductoExistente(5)).thenReturn(producto);
        when(ingresoPersistencePort.saveIngreso(any(Ingreso.class)))
                .thenAnswer(invocation -> {
                    Ingreso ingreso = invocation.getArgument(0);
                    if (ingreso.getIdIngreso() == null) {
                        ingreso.setIdIngreso(100);
                    }
                    return ingreso;
                });
        when(ingresoPersistencePort.saveDetalles(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IngresoDTO resultado = ingresoUseCase.registrarIngresoCompleto(request);

        assertThat(resultado.getIdIngreso()).isEqualTo(100);
        assertThat(resultado.getIdUsuario()).isEqualTo(7);
        assertThat(resultado.getUsername()).isEqualTo("hwongu");
        assertThat(resultado.getFechaIngreso()).isEqualTo(FECHA_FIJA);
        assertThat(resultado.getEstado()).isEqualTo("RECIBIDO");

        ArgumentCaptor<Ingreso> ingresoCaptor = ArgumentCaptor.forClass(Ingreso.class);
        verify(ingresoPersistencePort, times(2)).saveIngreso(ingresoCaptor.capture());
        assertThat(ingresoCaptor.getAllValues().getLast().getEstado()).isEqualTo("RECIBIDO");

        ArgumentCaptor<List<DetalleIngreso>> detallesCaptor = ArgumentCaptor.forClass(List.class);
        verify(stockCatalogoPort).actualizarStock(detallesCaptor.capture(), org.mockito.ArgumentMatchers.eq("SUMAR"));
        assertThat(detallesCaptor.getValue().getFirst().getCantidad()).isEqualTo(4);
    }

    @Test
    void registrarIngresoCompleto_debeRechazarCuandoElUsuarioEstaInactivo() {
        RegistrarIngresoRequestDTO request = crearSolicitudRegistro(7, 5, 4, 19.5);
        when(usuarioRemotoPort.validarUsuarioActivo(7))
                .thenThrow(new BadRequestException("El usuario indicado no existe o esta inactivo."));

        assertThatThrownBy(() -> ingresoUseCase.registrarIngresoCompleto(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El usuario indicado no existe o esta inactivo.");

        verify(ingresoPersistencePort, never()).saveIngreso(any(Ingreso.class));
        verify(ingresoPersistencePort, never()).saveDetalles(anyList());
    }

    @Test
    void registrarIngresoCompleto_debeRechazarCuandoUnProductoNoExiste() {
        RegistrarIngresoRequestDTO request = crearSolicitudRegistro(7, 5, 4, 19.5);
        when(usuarioRemotoPort.validarUsuarioActivo(7)).thenReturn(crearUsuarioRemoto(7, "hwongu", true));
        when(productoRemotoPort.validarProductoExistente(5))
                .thenThrow(new BadRequestException("El producto indicado no existe."));

        assertThatThrownBy(() -> ingresoUseCase.registrarIngresoCompleto(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El producto indicado no existe.");

        verify(ingresoPersistencePort, never()).saveIngreso(any(Ingreso.class));
        verify(ingresoPersistencePort, never()).saveDetalles(anyList());
    }

    @Test
    void registrarIngresoCompleto_debeMarcarErrorIntegracionCuandoFallaActualizacionRemotaDeStock() {
        RegistrarIngresoRequestDTO request = crearSolicitudRegistro(7, 5, 4, 19.5);
        when(usuarioRemotoPort.validarUsuarioActivo(7)).thenReturn(crearUsuarioRemoto(7, "hwongu", true));
        when(productoRemotoPort.validarProductoExistente(5)).thenReturn(crearProductoRemoto(5, "Monitor"));
        when(ingresoPersistencePort.saveIngreso(any(Ingreso.class)))
                .thenAnswer(invocation -> {
                    Ingreso ingreso = invocation.getArgument(0);
                    if (ingreso.getIdIngreso() == null) {
                        ingreso.setIdIngreso(100);
                    }
                    return ingreso;
                });
        when(ingresoPersistencePort.saveDetalles(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.doThrow(new RestClientException("catalogo caido") { })
                .when(stockCatalogoPort).actualizarStock(anyList(), any(String.class));

        assertThatThrownBy(() -> ingresoUseCase.registrarIngresoCompleto(request))
                .isInstanceOf(IntegracionRemotaException.class)
                .hasMessage("El ingreso fue registrado localmente, pero no se pudo actualizar el stock en catalogo. El ingreso quedo en estado ERROR_INTEGRACION.");

        ArgumentCaptor<Ingreso> ingresoCaptor = ArgumentCaptor.forClass(Ingreso.class);
        verify(ingresoPersistencePort, times(2)).saveIngreso(ingresoCaptor.capture());
        assertThat(ingresoCaptor.getAllValues().getLast().getEstado()).isEqualTo("ERROR_INTEGRACION");
    }

    @Test
    void anularIngreso_debeRevertirStockYMarcarIngresoComoAnulado() {
        Ingreso ingreso = crearIngreso(50, 7, FECHA_FIJA, "RECIBIDO");
        DetalleIngreso detalle = crearDetalle(60, 50, 5, 4, 19.5);

        when(ingresoPersistencePort.findIngresoById(50)).thenReturn(Optional.of(ingreso));
        when(ingresoPersistencePort.findDetallesByIngresoId(50)).thenReturn(List.of(detalle));

        ingresoUseCase.anularIngreso(50);

        ArgumentCaptor<Ingreso> ingresoCaptor = ArgumentCaptor.forClass(Ingreso.class);
        verify(ingresoPersistencePort).saveIngreso(ingresoCaptor.capture());
        assertThat(ingresoCaptor.getValue().getEstado()).isEqualTo("ANULADO");
        verify(stockCatalogoPort).actualizarStock(List.of(detalle), "RESTAR");
    }

    @Test
    void anularIngreso_debeLanzarExcepcionCuandoIngresoNoExiste() {
        when(ingresoPersistencePort.findIngresoById(50)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingresoUseCase.anularIngreso(50))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Ingreso no encontrado.");
    }

    @Test
    void anularIngreso_debeRechazarCuandoYaEstaAnulado() {
        Ingreso ingreso = crearIngreso(50, 7, FECHA_FIJA, "ANULADO");
        when(ingresoPersistencePort.findIngresoById(50)).thenReturn(Optional.of(ingreso));

        assertThatThrownBy(() -> ingresoUseCase.anularIngreso(50))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El ingreso ya se encuentra anulado.");

        verify(ingresoPersistencePort, never()).findDetallesByIngresoId(any());
    }

    @Test
    void anularIngreso_debeRechazarCuandoElEstadoNoPermiteAnular() {
        Ingreso ingreso = crearIngreso(50, 7, FECHA_FIJA, "PENDIENTE");
        when(ingresoPersistencePort.findIngresoById(50)).thenReturn(Optional.of(ingreso));

        assertThatThrownBy(() -> ingresoUseCase.anularIngreso(50))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Solo se puede anular un ingreso en estado RECIBIDO.");
    }

    @Test
    void anularIngreso_debeMarcarErrorIntegracionCuandoFallaLaReversionDeStock() {
        Ingreso ingreso = crearIngreso(50, 7, FECHA_FIJA, "RECIBIDO");
        DetalleIngreso detalle = crearDetalle(60, 50, 5, 4, 19.5);

        when(ingresoPersistencePort.findIngresoById(50)).thenReturn(Optional.of(ingreso));
        when(ingresoPersistencePort.findDetallesByIngresoId(50)).thenReturn(List.of(detalle));
        org.mockito.Mockito.doThrow(new RestClientException("catalogo caido") { })
                .when(stockCatalogoPort).actualizarStock(anyList(), any(String.class));

        assertThatThrownBy(() -> ingresoUseCase.anularIngreso(50))
                .isInstanceOf(IntegracionRemotaException.class)
                .hasMessage("No se pudo revertir el stock en catalogo. El ingreso quedo en estado ERROR_INTEGRACION.");

        ArgumentCaptor<Ingreso> ingresoCaptor = ArgumentCaptor.forClass(Ingreso.class);
        verify(ingresoPersistencePort).saveIngreso(ingresoCaptor.capture());
        assertThat(ingresoCaptor.getValue().getEstado()).isEqualTo("ERROR_INTEGRACION");
    }

    @Test
    void actualizarEstadoIngreso_debeNormalizarYGuardarElNuevoEstado() {
        Ingreso ingreso = crearIngreso(70, 9, FECHA_FIJA, "PENDIENTE");
        when(ingresoPersistencePort.findIngresoById(70)).thenReturn(Optional.of(ingreso));

        ingresoUseCase.actualizarEstadoIngreso(70, " recibido ");

        ArgumentCaptor<Ingreso> ingresoCaptor = ArgumentCaptor.forClass(Ingreso.class);
        verify(ingresoPersistencePort).saveIngreso(ingresoCaptor.capture());
        assertThat(ingresoCaptor.getValue().getEstado()).isEqualTo("RECIBIDO");
    }

    @Test
    void actualizarEstadoIngreso_debeLanzarExcepcionCuandoIngresoNoExiste() {
        when(ingresoPersistencePort.findIngresoById(70)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingresoUseCase.actualizarEstadoIngreso(70, "RECIBIDO"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Ingreso no encontrado.");
    }

    private RegistrarIngresoRequestDTO crearSolicitudRegistro(Integer idUsuario, Integer idProducto, Integer cantidad, Double precioCompra) {
        IngresoDTO ingreso = IngresoDTO.builder()
                .idUsuario(idUsuario)
                .fechaIngreso(FECHA_FIJA)
                .build();

        DetalleIngresoDTO detalle = DetalleIngresoDTO.builder()
                .idProducto(idProducto)
                .cantidad(cantidad)
                .precioCompra(precioCompra)
                .build();

        return RegistrarIngresoRequestDTO.builder()
                .ingreso(ingreso)
                .detalles(List.of(detalle))
                .build();
    }

    private Ingreso crearIngreso(Integer idIngreso, Integer idUsuario, LocalDateTime fechaIngreso, String estado) {
        return Ingreso.builder()
                .idIngreso(idIngreso)
                .idUsuario(idUsuario)
                .fechaIngreso(fechaIngreso)
                .estado(estado)
                .build();
    }

    private DetalleIngreso crearDetalle(Integer idDetalle, Integer idIngreso, Integer idProducto, Integer cantidad, Double precioCompra) {
        return DetalleIngreso.builder()
                .idDetalle(idDetalle)
                .idIngreso(idIngreso)
                .idProducto(idProducto)
                .cantidad(cantidad)
                .precioCompra(precioCompra)
                .build();
    }

    private UsuarioRemotoDTO crearUsuarioRemoto(Integer idUsuario, String username, Boolean estado) {
        return UsuarioRemotoDTO.builder()
                .idUsuario(idUsuario)
                .username(username)
                .estado(estado)
                .build();
    }

    private ProductoRemotoDTO crearProductoRemoto(Integer idProducto, String nombre) {
        return ProductoRemotoDTO.builder()
                .idProducto(idProducto)
                .nombre(nombre)
                .build();
    }
}
