package net.hwongu.nexus.catalogo.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.port.in.CategoriaUseCase;
import net.hwongu.nexus.catalogo.be.dto.CategoriaDTO;
import net.hwongu.nexus.catalogo.be.exception.GlobalExceptionHandler;
import net.hwongu.nexus.catalogo.be.exception.RecursoNoEncontradoException;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.in.web.CategoriaController;
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
 * Verifica el comportamiento HTTP de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaUseCase categoriaUseCase;

    @InjectMocks
    private CategoriaController categoriaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listarCategorias_debeRetornarContratoJsonEsperado() throws Exception {
        when(categoriaUseCase.listarCategorias()).thenReturn(List.of(
                crearCategoria(1, "Laptops", "Equipos portatiles"),
                crearCategoria(2, "Monitores", "Pantallas")
        ));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptops"))
                .andExpect(jsonPath("$[0].descripcion").value("Equipos portatiles"))
                .andExpect(jsonPath("$[1].idCategoria").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Monitores"))
                .andExpect(jsonPath("$[1].descripcion").value("Pantallas"));

        verify(categoriaUseCase).listarCategorias();
    }

    @Test
    void buscarCategoriaPorId_debeRetornarContratoJsonEsperado() throws Exception {
        when(categoriaUseCase.buscarCategoriaPorId(1))
                .thenReturn(crearCategoria(1, "Laptops", "Equipos portatiles"));

        mockMvc.perform(get("/api/categorias/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptops"))
                .andExpect(jsonPath("$.descripcion").value("Equipos portatiles"));

        verify(categoriaUseCase).buscarCategoriaPorId(1);
    }

    @Test
    void buscarCategoriaPorId_debeRetornarError404ConFormatoEstandar() throws Exception {
        when(categoriaUseCase.buscarCategoriaPorId(99))
                .thenThrow(new RecursoNoEncontradoException("Categoria no encontrada."));

        mockMvc.perform(get("/api/categorias/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Categoria no encontrada."))
                .andExpect(jsonPath("$.path").value("/api/categorias/99"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaUseCase).buscarCategoriaPorId(99);
    }

    @Test
    void registrarCategoria_debeAceptarJsonValidoYRetornarContratoCreado() throws Exception {
        CategoriaDTO request = CategoriaDTO.builder()
                .nombre("Laptops")
                .descripcion("Equipos portatiles")
                .build();
        when(categoriaUseCase.registrarCategoria(any(Categoria.class)))
                .thenReturn(crearCategoria(1, "Laptops", "Equipos portatiles"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptops"))
                .andExpect(jsonPath("$.descripcion").value("Equipos portatiles"));

        verify(categoriaUseCase).registrarCategoria(any(Categoria.class));
    }

    @Test
    void registrarCategoria_debeRetornarError400CuandoBodyEsInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "nombre": "   ",
                  "descripcion": "Equipos portatiles"
                }
                """;

        mockMvc.perform(post("/api/categorias")
                        .contentType(APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.nombre").value("El nombre de la categoria es obligatorio."))
                .andExpect(jsonPath("$.path").value("/api/categorias"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaUseCase, never()).registrarCategoria(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_debeAceptarJsonValidoYRetornarMensajeEsperado() throws Exception {
        CategoriaDTO request = CategoriaDTO.builder()
                .nombre("Monitores")
                .descripcion("Pantallas")
                .build();
        doNothing().when(categoriaUseCase).actualizarCategoria(any(Integer.class), any(Categoria.class));

        mockMvc.perform(put("/api/categorias/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Categoria actualizada exitosamente"));

        verify(categoriaUseCase).actualizarCategoria(any(Integer.class), any(Categoria.class));
    }

    @Test
    void actualizarCategoria_debeRetornarError404CuandoNoExiste() throws Exception {
        CategoriaDTO request = CategoriaDTO.builder()
                .nombre("Monitores")
                .descripcion("Pantallas")
                .build();
        doThrow(new RecursoNoEncontradoException("Categoria no encontrada."))
                .when(categoriaUseCase).actualizarCategoria(any(Integer.class), any(Categoria.class));

        mockMvc.perform(put("/api/categorias/{id}", 88)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Categoria no encontrada."))
                .andExpect(jsonPath("$.path").value("/api/categorias/88"));

        verify(categoriaUseCase).actualizarCategoria(any(Integer.class), any(Categoria.class));
    }

    @Test
    void eliminarCategoria_debeRetornarNoContent() throws Exception {
        doNothing().when(categoriaUseCase).eliminarCategoria(1);

        mockMvc.perform(delete("/api/categorias/{id}", 1))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoriaUseCase).eliminarCategoria(1);
    }

    @Test
    void eliminarCategoria_debeRetornarError409CuandoHayConflictoReferencial() throws Exception {
        doThrow(new DataIntegrityViolationException("No se puede eliminar la categoria porque tiene productos asociados."))
                .when(categoriaUseCase).eliminarCategoria(1);

        mockMvc.perform(delete("/api/categorias/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("No se puede eliminar la categoria porque tiene productos asociados."))
                .andExpect(jsonPath("$.path").value("/api/categorias/1"));

        verify(categoriaUseCase).eliminarCategoria(1);
    }

    private Categoria crearCategoria(Integer id, String nombre, String descripcion) {
        return Categoria.builder()
                .idCategoria(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build();
    }
}
