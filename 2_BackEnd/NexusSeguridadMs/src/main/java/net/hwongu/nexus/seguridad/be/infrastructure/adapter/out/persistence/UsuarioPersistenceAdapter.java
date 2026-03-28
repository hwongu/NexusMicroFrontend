package net.hwongu.nexus.seguridad.be.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.seguridad.be.domain.model.Usuario;
import net.hwongu.nexus.seguridad.be.domain.port.out.UsuarioPersistencePort;
import net.hwongu.nexus.seguridad.be.infrastructure.adapter.out.persistence.entity.UsuarioJpaEntity;
import net.hwongu.nexus.seguridad.be.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioPersistencePort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public List<Usuario> findAllOrdered() {
        return usuarioJpaRepository.findAllByOrderByIdUsuarioAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioJpaEntity saved = usuarioJpaRepository.save(toEntity(usuario));
        return toDomain(saved);
    }

    @Override
    public boolean existsById(Integer id) {
        return usuarioJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        usuarioJpaRepository.deleteById(id);
    }

    @Override
    public void flush() {
        usuarioJpaRepository.flush();
    }

    @Override
    public Optional<Usuario> findActiveByCredentials(String username, String password) {
        return usuarioJpaRepository.findByUsernameAndPasswordAndEstadoTrue(username, password)
                .map(this::toDomain);
    }

    private Usuario toDomain(UsuarioJpaEntity entity) {
        return Usuario.builder()
                .idUsuario(entity.getIdUsuario())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .estado(entity.getEstado())
                .build();
    }

    private UsuarioJpaEntity toEntity(Usuario usuario) {
        return UsuarioJpaEntity.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .estado(usuario.getEstado())
                .build();
    }
}
