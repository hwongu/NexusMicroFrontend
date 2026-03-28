package net.hwongu.nexus.seguridad.be.service;

import net.hwongu.nexus.seguridad.be.application.usecase.UsuarioUseCaseImpl;
import net.hwongu.nexus.seguridad.be.domain.model.Usuario;
import net.hwongu.nexus.seguridad.be.domain.port.out.UsuarioPersistencePort;
import net.hwongu.nexus.seguridad.be.exception.NoAutorizadoException;
import net.hwongu.nexus.seguridad.be.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifica los casos de uso de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseImplTest {

    @Mock
    private UsuarioPersistencePort usuarioPersistencePort;

    @InjectMocks
    private UsuarioUseCaseImpl usuarioUseCase;

    @Test
    void listarUsuarios_debeRetornarUsuariosDelDominio() {
        Usuario usuario1 = crearUsuario(1, "hwongu", "clave", true);
        Usuario usuario2 = crearUsuario(2, "mlopez", "secreto", false);
        when(usuarioPersistencePort.findAllOrdered()).thenReturn(List.of(usuario1, usuario2));

        List<Usuario> resultado = usuarioUseCase.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getIdUsuario());
        assertEquals("hwongu", resultado.get(0).getUsername());
        assertEquals(true, resultado.get(0).getEstado());
        assertEquals(2, resultado.get(1).getIdUsuario());
        assertEquals("mlopez", resultado.get(1).getUsername());
        assertEquals(false, resultado.get(1).getEstado());
        verify(usuarioPersistencePort).findAllOrdered();
    }

    @Test
    void listarUsuarios_debeRetornarListaVaciaCuandoNoHayRegistros() {
        when(usuarioPersistencePort.findAllOrdered()).thenReturn(List.of());

        List<Usuario> resultado = usuarioUseCase.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(usuarioPersistencePort).findAllOrdered();
    }

    @Test
    void buscarUsuarioPorId_debeRetornarUsuarioCuandoExiste() {
        Usuario usuario = crearUsuario(1, "hwongu", "clave", true);
        when(usuarioPersistencePort.findById(1)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioUseCase.buscarUsuarioPorId(1);

        assertEquals(1, resultado.getIdUsuario());
        assertEquals("hwongu", resultado.getUsername());
        assertEquals(true, resultado.getEstado());
        verify(usuarioPersistencePort).findById(1);
    }

    @Test
    void buscarUsuarioPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(usuarioPersistencePort.findById(99)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> usuarioUseCase.buscarUsuarioPorId(99)
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(usuarioPersistencePort).findById(99);
    }

    @Test
    void registrarUsuario_debeGuardarUsuarioConIdNuloYRetornarDominio() {
        Usuario request = crearUsuario(88, "hwongu", "clave", true);
        Usuario usuarioGuardado = crearUsuario(5, "hwongu", "clave", true);
        when(usuarioPersistencePort.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioUseCase.registrarUsuario(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioPersistencePort).save(captor.capture());
        Usuario usuarioEnviadoAGuardar = captor.getValue();

        assertNull(usuarioEnviadoAGuardar.getIdUsuario());
        assertEquals("hwongu", usuarioEnviadoAGuardar.getUsername());
        assertEquals("clave", usuarioEnviadoAGuardar.getPassword());
        assertEquals(true, usuarioEnviadoAGuardar.getEstado());
        assertEquals(5, resultado.getIdUsuario());
        assertEquals("hwongu", resultado.getUsername());
        assertEquals(true, resultado.getEstado());
    }

    @Test
    void actualizarUsuario_debeActualizarDatosCuandoExiste() {
        Usuario usuarioExistente = crearUsuario(3, "hwongu", "clave", true);
        Usuario request = crearUsuario(null, "mlopez", "nueva-clave", false);
        when(usuarioPersistencePort.findById(3)).thenReturn(Optional.of(usuarioExistente));

        usuarioUseCase.actualizarUsuario(3, request);

        verify(usuarioPersistencePort).findById(3);
        verify(usuarioPersistencePort).save(usuarioExistente);
        assertEquals("mlopez", usuarioExistente.getUsername());
        assertEquals("nueva-clave", usuarioExistente.getPassword());
        assertEquals(false, usuarioExistente.getEstado());
    }

    @Test
    void actualizarUsuario_debeLanzarExcepcionCuandoNoExiste() {
        Usuario request = crearUsuario(null, "mlopez", "nueva-clave", false);
        when(usuarioPersistencePort.findById(7)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> usuarioUseCase.actualizarUsuario(7, request)
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(usuarioPersistencePort).findById(7);
        verify(usuarioPersistencePort, never()).save(any(Usuario.class));
    }

    @Test
    void eliminarUsuario_debeEliminarYHacerFlushCuandoExiste() {
        when(usuarioPersistencePort.existsById(4)).thenReturn(true);

        usuarioUseCase.eliminarUsuario(4);

        verify(usuarioPersistencePort).existsById(4);
        verify(usuarioPersistencePort).deleteById(4);
        verify(usuarioPersistencePort).flush();
    }

    @Test
    void eliminarUsuario_debeLanzarExcepcionCuandoNoExiste() {
        when(usuarioPersistencePort.existsById(10)).thenReturn(false);

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> usuarioUseCase.eliminarUsuario(10)
        );

        assertEquals("Usuario no encontrado.", exception.getMessage());
        verify(usuarioPersistencePort).existsById(10);
        verify(usuarioPersistencePort, never()).deleteById(10);
        verify(usuarioPersistencePort, never()).flush();
    }

    @Test
    void eliminarUsuario_debeTraducirErrorDeIntegridadReferencial() {
        when(usuarioPersistencePort.existsById(4)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("fk")).when(usuarioPersistencePort).flush();

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> usuarioUseCase.eliminarUsuario(4)
        );

        assertEquals(
                "No se puede eliminar el usuario porque tiene ingresos registrados a su nombre.",
                exception.getMessage()
        );
        verify(usuarioPersistencePort).deleteById(4);
        verify(usuarioPersistencePort).flush();
    }

    @Test
    void autenticarUsuario_debeRetornarUsuarioCuandoCredencialesSonValidas() {
        Usuario usuario = crearUsuario(1, "hwongu", "clave", true);
        when(usuarioPersistencePort.findActiveByCredentials("hwongu", "clave"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioUseCase.autenticarUsuario("hwongu", "clave");

        assertEquals(1, resultado.getIdUsuario());
        assertEquals("hwongu", resultado.getUsername());
        assertEquals(true, resultado.getEstado());
        verify(usuarioPersistencePort).findActiveByCredentials("hwongu", "clave");
    }

    @Test
    void autenticarUsuario_debeLanzarExcepcionCuandoCredencialesSonInvalidas() {
        when(usuarioPersistencePort.findActiveByCredentials("hwongu", "incorrecta"))
                .thenReturn(Optional.empty());

        NoAutorizadoException exception = assertThrows(
                NoAutorizadoException.class,
                () -> usuarioUseCase.autenticarUsuario("hwongu", "incorrecta")
        );

        assertEquals("Credenciales invalidas.", exception.getMessage());
        verify(usuarioPersistencePort).findActiveByCredentials("hwongu", "incorrecta");
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
