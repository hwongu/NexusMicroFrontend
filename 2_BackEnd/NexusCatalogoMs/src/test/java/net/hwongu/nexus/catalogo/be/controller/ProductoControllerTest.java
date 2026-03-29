package net.hwongu.nexus.catalogo.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwongu.nexus.catalogo.be.application.usecase.command.ActualizarStockCommand;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;
import net.hwongu.nexus.catalogo.be.domain.port.in.ProductoUseCase;
import net.hwongu.nexus.catalogo.be.dto.ActualizarStockRequestDTO;
import net.hwongu.nexus.catalogo.be.dto.ProductoDTO;
import net.hwongu.nexus.catalogo.be.exception.BadRequestException;
import net.hwongu.nexus.catalogo.be.exception.GlobalExceptionHandler;
import net.hwongu.nexus.catalogo.be.exception.RecursoNoEncontradoException;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.in.web.ProductoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica el comportamiento HTTP de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoUseCase productoUseCase;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listarProductos_debeRetornarContratoJsonEsperado() throws Exception {
        when(productoUseCase.listarProductos()).thenReturn(List.of(
                crearProducto(1, 10, "Laptops", "ThinkPad T14", 1200.5, 50)
        ));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idProducto").value(1))
                .andExpect(jsonPath("$[0].idCategoria").value(10))
                .andExpect(jsonPath("$[0].nombreCategoria").value("Laptops"))
                .andExpect(jsonPath("$[0].nombre").value("ThinkPad T14"))
                .andExpect(jsonPath("$[0].precio").value(1200.5))
                .andExpect(jsonPath("$[0].stock").value(50));

        verify(productoUseCase).listarProductos();
    }

    @Test
    void buscarProductoPorId_debeRetornarContratoJsonEsperado() throws Exception {
        when(productoUseCase.buscarProductoPorId(1))
                .thenReturn(crearProducto(1, 10, "Laptops", "ThinkPad T14", 1200.5, 50));

        mockMvc.perform(get("/api/productos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idProducto").value(1))
                .andExpect(jsonPath("$.idCategoria").value(10))
                .andExpect(jsonPath("$.nombreCategoria").value("Laptops"))
                .andExpect(jsonPath("$.nombre").value("ThinkPad T14"))
                .andExpect(jsonPath("$.precio").value(1200.5))
                .andExpect(jsonPath("$.stock").value(50));

        verify(productoUseCase).buscarProductoPorId(1);
    }

    @Test
    void buscarProductoPorId_debeRetornarError404ConFormatoEstandar() throws Exception {
        when(productoUseCase.buscarProductoPorId(77))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado."));

        mockMvc.perform(get("/api/productos/{id}", 77))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado."))
                .andExpect(jsonPath("$.path").value("/api/productos/77"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(productoUseCase).buscarProductoPorId(77);
    }

    @Test
    void registrarProducto_debeAceptarJsonValidoYRetornarContratoCreado() throws Exception {
        ProductoDTO request = ProductoDTO.builder()
                .idCategoria(10)
                .nombre("ThinkPad T14")
                .precio(1200.5)
                .stock(50)
                .build();
        when(productoUseCase.registrarProducto(any(Producto.class)))
                .thenReturn(crearProducto(1, 10, "Laptops", "ThinkPad T14", 1200.5, 50));

        mockMvc.perform(post("/api/productos")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idProducto").value(1))
                .andExpect(jsonPath("$.idCategoria").value(10))
                .andExpect(jsonPath("$.nombreCategoria").value("Laptops"))
                .andExpect(jsonPath("$.nombre").value("ThinkPad T14"))
                .andExpect(jsonPath("$.precio").value(1200.5))
                .andExpect(jsonPath("$.stock").value(50));

        verify(productoUseCase).registrarProducto(any(Producto.class));
    }

    @Test
    void registrarProducto_debeRetornarError400CuandoBodyEsInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "idCategoria": 0,
                  "nombre": "",
                  "precio": 0,
                  "stock": -1
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.idCategoria").value("El id de la categoria debe ser mayor a cero."))
                .andExpect(jsonPath("$.message.nombre").value("El nombre del producto es obligatorio."))
                .andExpect(jsonPath("$.message.precio").value("El precio debe ser mayor a cero."))
                .andExpect(jsonPath("$.message.stock").value("El stock no puede ser negativo."))
                .andExpect(jsonPath("$.path").value("/api/productos"));

        verify(productoUseCase, never()).registrarProducto(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeAceptarJsonValidoYRetornarMensajeEsperado() throws Exception {
        ProductoDTO request = ProductoDTO.builder()
                .idCategoria(10)
                .nombre("ThinkPad X1")
                .precio(1800.0)
                .stock(15)
                .build();
        doNothing().when(productoUseCase).actualizarProducto(any(Integer.class), any(Producto.class));

        mockMvc.perform(put("/api/productos/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Producto actualizado exitosamente"));

        verify(productoUseCase).actualizarProducto(any(Integer.class), any(Producto.class));
    }

    @Test
    void actualizarProducto_debeRetornarError404CuandoNoExiste() throws Exception {
        ProductoDTO request = ProductoDTO.builder()
                .idCategoria(10)
                .nombre("ThinkPad X1")
                .precio(1800.0)
                .stock(15)
                .build();
        doThrow(new RecursoNoEncontradoException("Producto no encontrado."))
                .when(productoUseCase).actualizarProducto(any(Integer.class), any(Producto.class));

        mockMvc.perform(put("/api/productos/{id}", 99)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado."))
                .andExpect(jsonPath("$.path").value("/api/productos/99"));

        verify(productoUseCase).actualizarProducto(any(Integer.class), any(Producto.class));
    }

    @Test
    void actualizarStockProducto_debeAceptarJsonValidoYRetornarMensajeEsperado() throws Exception {
        ActualizarStockRequestDTO request = ActualizarStockRequestDTO.builder()
                .cantidad(10)
                .operacion("SUMAR")
                .build();
        doNothing().when(productoUseCase).actualizarStockProducto(any(Integer.class), any(ActualizarStockCommand.class));

        mockMvc.perform(put("/api/productos/{id}/stock", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Stock del producto actualizado exitosamente"));

        verify(productoUseCase).actualizarStockProducto(any(Integer.class), any(ActualizarStockCommand.class));
    }

    @Test
    void actualizarStockProducto_debeRetornarError400CuandoOperacionEsInvalida() throws Exception {
        ActualizarStockRequestDTO request = ActualizarStockRequestDTO.builder()
                .cantidad(10)
                .operacion("DIVIDIR")
                .build();
        doThrow(new BadRequestException("La operacion debe ser SUMAR o RESTAR."))
                .when(productoUseCase).actualizarStockProducto(any(Integer.class), any(ActualizarStockCommand.class));

        mockMvc.perform(put("/api/productos/{id}/stock", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("La operacion debe ser SUMAR o RESTAR."))
                .andExpect(jsonPath("$.path").value("/api/productos/1/stock"));

        verify(productoUseCase).actualizarStockProducto(any(Integer.class), any(ActualizarStockCommand.class));
    }

    @Test
    void eliminarProducto_debeRetornarNoContent() throws Exception {
        doNothing().when(productoUseCase).eliminarProducto(1);

        mockMvc.perform(delete("/api/productos/{id}", 1))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(productoUseCase).eliminarProducto(1);
    }

    @Test
    void eliminarProducto_debeRetornarError409CuandoHayConflictoReferencial() throws Exception {
        doThrow(new DataIntegrityViolationException("No se puede eliminar el producto porque esta referenciado en un ingreso."))
                .when(productoUseCase).eliminarProducto(1);

        mockMvc.perform(delete("/api/productos/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("No se puede eliminar el producto porque esta referenciado en un ingreso."))
                .andExpect(jsonPath("$.path").value("/api/productos/1"));

        verify(productoUseCase).eliminarProducto(1);
    }

    private Producto crearProducto(
            Integer idProducto,
            Integer idCategoria,
            String nombreCategoria,
            String nombre,
            Double precio,
            Integer stock
    ) {
        return Producto.builder()
                .idProducto(idProducto)
                .categoria(Categoria.builder().idCategoria(idCategoria).nombre(nombreCategoria).build())
                .nombre(nombre)
                .precio(precio)
                .stock(stock)
                .build();
    }
}
