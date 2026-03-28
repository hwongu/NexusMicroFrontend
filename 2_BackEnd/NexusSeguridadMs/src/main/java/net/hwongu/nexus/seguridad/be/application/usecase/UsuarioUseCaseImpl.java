package net.hwongu.nexus.seguridad.be.application.usecase;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.seguridad.be.domain.model.Usuario;
import net.hwongu.nexus.seguridad.be.domain.port.in.UsuarioUseCase;
import net.hwongu.nexus.seguridad.be.domain.port.out.UsuarioPersistencePort;
import net.hwongu.nexus.seguridad.be.exception.NoAutorizadoException;
import net.hwongu.nexus.seguridad.be.exception.RecursoNoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina los casos de uso de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Service
@RequiredArgsConstructor
public class UsuarioUseCaseImpl implements UsuarioUseCase {

    private final UsuarioPersistencePort usuarioPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioPersistencePort.findAllOrdered();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Integer id) {
        return usuarioPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setIdUsuario(null);
        return usuarioPersistencePort.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarUsuario(Integer id, Usuario usuario) {
        Usuario usuarioExistente = usuarioPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setEstado(usuario.getEstado());

        usuarioPersistencePort.save(usuarioExistente);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Integer id) {
        if (!usuarioPersistencePort.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado.");
        }

        try {
            usuarioPersistencePort.deleteById(id);
            usuarioPersistencePort.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException(
                    "No se puede eliminar el usuario porque tiene ingresos registrados a su nombre.",
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario autenticarUsuario(String username, String password) {
        return usuarioPersistencePort.findActiveByCredentials(username, password)
                .orElseThrow(() -> new NoAutorizadoException("Credenciales invalidas."));
    }
}
