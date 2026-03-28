package net.hwongu.nexus.seguridad.be.domain.port.out;

import net.hwongu.nexus.seguridad.be.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Define el acceso a datos de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface UsuarioPersistencePort {

    List<Usuario> findAllOrdered();

    Optional<Usuario> findById(Integer id);

    Usuario save(Usuario usuario);

    boolean existsById(Integer id);

    void deleteById(Integer id);

    void flush();

    Optional<Usuario> findActiveByCredentials(String username, String password);
}
