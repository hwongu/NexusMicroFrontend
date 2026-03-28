package net.hwongu.nexus.seguridad.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwongu.nexus.seguridad.be.domain.model.Usuario;
import net.hwongu.nexus.seguridad.be.domain.port.in.UsuarioUseCase;
import net.hwongu.nexus.seguridad.be.dto.LoginRequestDTO;
import net.hwongu.nexus.seguridad.be.dto.UsuarioDTO;
import net.hwongu.nexus.seguridad.be.exception.GlobalExceptionHandler;
import net.hwongu.nexus.seguridad.be.exception.NoAutorizadoException;
import net.hwongu.nexus.seguridad.be.exception.RecursoNoEncontradoException;
import net.hwongu.nexus.seguridad.be.infrastructure.adapter.in.web.UsuarioController;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
 * Verifica el comportamiento HTTP de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioUseCase usuarioUseCase;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listarUsuarios_debeRetornarContratoJsonEsperado() throws Exception {
        when(usuarioUseCase.listarUsuarios()).thenReturn(List.of(
                crearUsuario(1, "hwongu", "clave", true),
                crearUsuario(2, "mlopez", "secreto", false)
        ));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].username").value("hwongu"))
                .andExpect(jsonPath("$[0].estado").value(true))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].idUsuario").value(2))
                .andExpect(jsonPath("$[1].username").value("mlopez"))
                .andExpect(jsonPath("$[1].estado").value(false));

        verify(usuarioUseCase).listarUsuarios();
    }

    @Test
    void buscarUsuarioPorId_debeRetornarContratoJsonEsperado() throws Exception {
        when(usuarioUseCase.buscarUsuarioPorId(1)).thenReturn(crearUsuario(1, "hwongu", "clave", true));

        mockMvc.perform(get("/api/usuarios/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.username").value("hwongu"))
                .andExpect(jsonPath("$.estado").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(usuarioUseCase).buscarUsuarioPorId(1);
    }

    @Test
    void buscarUsuarioPorId_debeRetornarError404ConFormatoEstandar() throws Exception {
        when(usuarioUseCase.buscarUsuarioPorId(99))
                .thenThrow(new RecursoNoEncontradoException("Usuario no encontrado."));

        mockMvc.perform(get("/api/usuarios/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."))
                .andExpect(jsonPath("$.path").value("/api/usuarios/99"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(usuarioUseCase).buscarUsuarioPorId(99);
    }

    @Test
    void registrarUsuario_debeAceptarJsonValidoYRetornarContratoCreado() throws Exception {
        String request = """
                {
                  "username": "hwongu",
                  "password": "123456",
                  "estado": true
                }
                """;
        when(usuarioUseCase.registrarUsuario(any(Usuario.class)))
                .thenReturn(crearUsuario(1, "hwongu", "123456", true));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.username").value("hwongu"))
                .andExpect(jsonPath("$.estado").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(usuarioUseCase).registrarUsuario(any(Usuario.class));
    }

    @Test
    void registrarUsuario_debeRetornarError400CuandoBodyEsInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "username": "   ",
                  "password": "",
                  "estado": null
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message.username").value("El username es obligatorio."))
                .andExpect(jsonPath("$.message.password").value("El password es obligatorio."))
                .andExpect(jsonPath("$.message.estado").value("El estado del usuario es obligatorio."))
                .andExpect(jsonPath("$.path").value("/api/usuarios"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(usuarioUseCase, never()).registrarUsuario(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_debeAceptarJsonValidoYRetornarMensajeEsperado() throws Exception {
        String request = """
                {
                  "username": "mlopez",
                  "password": "nueva-clave",
                  "estado": false
                }
                """;
        doNothing().when(usuarioUseCase).actualizarUsuario(anyInt(), any(Usuario.class));

        mockMvc.perform(put("/api/usuarios/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Usuario actualizado exitosamente"));

        verify(usuarioUseCase).actualizarUsuario(anyInt(), any(Usuario.class));
    }

    @Test
    void actualizarUsuario_debeRetornarError404CuandoNoExiste() throws Exception {
        String request = """
                {
                  "username": "mlopez",
                  "password": "nueva-clave",
                  "estado": false
                }
                """;
        doThrow(new RecursoNoEncontradoException("Usuario no encontrado."))
                .when(usuarioUseCase).actualizarUsuario(anyInt(), any(Usuario.class));

        mockMvc.perform(put("/api/usuarios/{id}", 88)
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."))
                .andExpect(jsonPath("$.path").value("/api/usuarios/88"));

        verify(usuarioUseCase).actualizarUsuario(anyInt(), any(Usuario.class));
    }

    @Test
    void eliminarUsuario_debeRetornarNoContent() throws Exception {
        doNothing().when(usuarioUseCase).eliminarUsuario(1);

        mockMvc.perform(delete("/api/usuarios/{id}", 1))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(usuarioUseCase).eliminarUsuario(1);
    }

    @Test
    void eliminarUsuario_debeRetornarError409CuandoHayConflictoReferencial() throws Exception {
        doThrow(new DataIntegrityViolationException("No se puede eliminar el usuario porque tiene ingresos registrados a su nombre."))
                .when(usuarioUseCase).eliminarUsuario(1);

        mockMvc.perform(delete("/api/usuarios/{id}", 1))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("No se puede eliminar el usuario porque tiene ingresos registrados a su nombre."))
                .andExpect(jsonPath("$.path").value("/api/usuarios/1"));

        verify(usuarioUseCase).eliminarUsuario(1);
    }

    @Test
    void login_debeAceptarJsonValidoYRetornarContratoEsperado() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("hwongu")
                .password("123456")
                .build();
        when(usuarioUseCase.autenticarUsuario(eq("hwongu"), eq("123456")))
                .thenReturn(crearUsuario(1, "hwongu", "123456", true));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.username").value("hwongu"))
                .andExpect(jsonPath("$.estado").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(usuarioUseCase).autenticarUsuario("hwongu", "123456");
    }

    @Test
    void login_debeRetornarError401CuandoCredencialesSonInvalidas() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("hwongu")
                .password("incorrecta")
                .build();
        when(usuarioUseCase.autenticarUsuario(eq("hwongu"), eq("incorrecta")))
                .thenThrow(new NoAutorizadoException("Credenciales invalidas."));

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Credenciales invalidas."))
                .andExpect(jsonPath("$.path").value("/api/usuarios/login"));

        verify(usuarioUseCase).autenticarUsuario("hwongu", "incorrecta");
    }

    private Usuario crearUsuario(Integer id, String username, String password, Boolean estado) {
        return Usuario.builder()
                .idUsuario(id)
                .username(username)
                .password(password)
                .estado(estado)
                .build();
    }
}
