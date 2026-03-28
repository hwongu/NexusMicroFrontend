package net.hwongu.nexus.seguridad.be.domain.port.in;

import net.hwongu.nexus.seguridad.be.domain.model.Usuario;

import java.util.List;

/**
 * Define los casos de uso de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface UsuarioUseCase {

    List<Usuario> listarUsuarios();

    Usuario buscarUsuarioPorId(Integer id);

    Usuario registrarUsuario(Usuario usuario);

    void actualizarUsuario(Integer id, Usuario usuario);

    void eliminarUsuario(Integer id);

    Usuario autenticarUsuario(String username, String password);
}
