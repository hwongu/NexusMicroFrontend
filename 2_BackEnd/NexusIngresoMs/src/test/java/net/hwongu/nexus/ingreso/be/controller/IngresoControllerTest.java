package net.hwongu.nexus.ingreso.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwongu.nexus.ingreso.be.domain.port.in.IngresoUseCase;
import net.hwongu.nexus.ingreso.be.dto.ActualizarEstadoIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.DetalleIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.IngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.RegistrarIngresoRequestDTO;
import net.hwongu.nexus.ingreso.be.exception.GlobalExceptionHandler;
import net.hwongu.nexus.ingreso.be.exception.IntegracionRemotaException;
import net.hwongu.nexus.ingreso.be.exception.RecursoNoEncontradoException;
import net.hwongu.nexus.ingreso.be.infrastructure.adapter.in.web.IngresoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica el comportamiento HTTP de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class IngresoControllerTest {

    @Mock
    private IngresoUseCase ingresoUseCase;

    @InjectMocks
    private IngresoController ingresoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(ingresoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listarIngresos_debeRetornarContratoJsonEsperado() throws Exception {
        IngresoDTO ingreso = IngresoDTO.builder()
                .idIngreso(10)
                .idUsuario(7)
                .username("hwongu")
                .fechaIngreso(LocalDateTime.of(2026, 3, 27, 10, 30))
                .estado("RECIBIDO")
                .build();

        when(ingresoUseCase.listarIngresos()).thenReturn(List.of(ingreso));

        mockMvc.perform(get("/api/ingresos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idIngreso").value(10))
                .andExpect(jsonPath("$[0].idUsuario").value(7))
                .andExpect(jsonPath("$[0].username").value("hwongu"))
                .andExpect(jsonPath("$[0].fechaIngreso").value("2026-03-27T10:30:00"))
                .andExpect(jsonPath("$[0].estado").value("RECIBIDO"));

        verify(ingresoUseCase).listarIngresos();
    }

    @Test
    void listarIngresos_debeRetornarError502ConFormatoEsperadoCuandoFallaIntegracion() throws Exception {
        when(ingresoUseCase.listarIngresos())
                .thenThrow(new IntegracionRemotaException("No se pudo consultar NexusSeguridadMs."));

        mockMvc.perform(get("/api/ingresos"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message").value("No se pudo consultar NexusSeguridadMs."))
                .andExpect(jsonPath("$.path").value("/api/ingresos"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void buscarDetallesPorIngreso_debeRetornarContratoJsonEsperado() throws Exception {
        DetalleIngresoDTO detalle = DetalleIngresoDTO.builder()
                .idDetalle(30)
                .idIngreso(10)
                .idProducto(5)
                .nombreProducto("Monitor")
                .cantidad(4)
                .precioCompra(19.5)
                .build();

        when(ingresoUseCase.buscarDetallesPorIngreso(10)).thenReturn(List.of(detalle));

        mockMvc.perform(get("/api/ingresos/{id}/detalles", 10))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idDetalle").value(30))
                .andExpect(jsonPath("$[0].idIngreso").value(10))
                .andExpect(jsonPath("$[0].idProducto").value(5))
                .andExpect(jsonPath("$[0].nombreProducto").value("Monitor"))
                .andExpect(jsonPath("$[0].cantidad").value(4))
                .andExpect(jsonPath("$[0].precioCompra").value(19.5));

        verify(ingresoUseCase).buscarDetallesPorIngreso(10);
    }

    @Test
    void buscarDetallesPorIngreso_debeRetornarError404ConFormatoEsperado() throws Exception {
        when(ingresoUseCase.buscarDetallesPorIngreso(10))
                .thenThrow(new RecursoNoEncontradoException("Ingreso no encontrado."));

        mockMvc.perform(get("/api/ingresos/{id}/detalles", 10))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Ingreso no encontrado."))
                .andExpect(jsonPath("$.path").value("/api/ingresos/10/detalles"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registrarIngreso_debeAceptarJsonValidoYRetornarContratoCreado() throws Exception {
        RegistrarIngresoRequestDTO request = RegistrarIngresoRequestDTO.builder()
                .ingreso(IngresoDTO.builder()
                        .idUsuario(7)
                        .fechaIngreso(LocalDateTime.of(2026, 3, 27, 10, 30))
                        .build())
                .detalles(List.of(DetalleIngresoDTO.builder()
                        .idProducto(5)
                        .cantidad(4)
                        .precioCompra(19.5)
                        .build()))
                .build();

        IngresoDTO response = IngresoDTO.builder()
                .idIngreso(100)
                .idUsuario(7)
                .username("hwongu")
                .fechaIngreso(LocalDateTime.of(2026, 3, 27, 10, 30))
                .estado("RECIBIDO")
                .build();

        when(ingresoUseCase.registrarIngresoCompleto(any(RegistrarIngresoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idIngreso").value(100))
                .andExpect(jsonPath("$.idUsuario").value(7))
                .andExpect(jsonPath("$.username").value("hwongu"))
                .andExpect(jsonPath("$.fechaIngreso").value("2026-03-27T10:30:00"))
                .andExpect(jsonPath("$.estado").value("RECIBIDO"));

        verify(ingresoUseCase).registrarIngresoCompleto(any(RegistrarIngresoRequestDTO.class));
    }

    @Test
    void registrarIngreso_debeRetornarError400CuandoBodyEsInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "ingreso": {
                    "idUsuario": 0
                  },
                  "detalles": [
                    {
                      "idProducto": null,
                      "cantidad": 0,
                      "precioCompra": 0
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message['ingreso.idUsuario']").value("El id del usuario debe ser mayor a cero."))
                .andExpect(jsonPath("$.message['detalles[0].idProducto']").value("El id del producto es obligatorio."))
                .andExpect(jsonPath("$.message['detalles[0].cantidad']").value("La cantidad debe ser mayor a cero."))
                .andExpect(jsonPath("$.message['detalles[0].precioCompra']").value("El precio de compra debe ser mayor a cero."))
                .andExpect(jsonPath("$.path").value("/api/ingresos"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void anularIngreso_debeRetornarMensajeDeConfirmacion() throws Exception {
        doNothing().when(ingresoUseCase).anularIngreso(50);

        mockMvc.perform(put("/api/ingresos/{id}/anular", 50))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Ingreso anulado exitosamente"));

        verify(ingresoUseCase).anularIngreso(50);
    }

    @Test
    void anularIngreso_debeRetornarError409CuandoHayConflicto() throws Exception {
        doThrow(new DataIntegrityViolationException("Conflicto de integridad."))
                .when(ingresoUseCase).anularIngreso(50);

        mockMvc.perform(put("/api/ingresos/{id}/anular", 50))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Conflicto de integridad."))
                .andExpect(jsonPath("$.path").value("/api/ingresos/50/anular"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void actualizarEstadoIngreso_debeAceptarJsonValidoYRetornarMensajeEsperado() throws Exception {
        ActualizarEstadoIngresoDTO request = ActualizarEstadoIngresoDTO.builder()
                .estado("RECIBIDO")
                .build();

        doNothing().when(ingresoUseCase).actualizarEstadoIngreso(50, "RECIBIDO");

        mockMvc.perform(put("/api/ingresos/{id}/estado", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Estado del ingreso actualizado correctamente"));

        verify(ingresoUseCase).actualizarEstadoIngreso(50, "RECIBIDO");
    }

    @Test
    void actualizarEstadoIngreso_debeRetornarError400CuandoBodyEsInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "estado": "   "
                }
                """;

        mockMvc.perform(put("/api/ingresos/{id}/estado", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.estado").value("El estado es obligatorio."))
                .andExpect(jsonPath("$.path").value("/api/ingresos/50/estado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
