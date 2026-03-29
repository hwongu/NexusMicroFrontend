package net.hwongu.nexus.seguridad.be.infrastructure.adapter.out.persistence.repository;

import net.hwongu.nexus.seguridad.be.infrastructure.adapter.out.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos JPA de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Integer> {

    List<UsuarioJpaEntity> findAllByOrderByIdUsuarioAsc();

    Optional<UsuarioJpaEntity> findByUsernameAndPasswordAndEstadoTrue(String username, String password);
}
