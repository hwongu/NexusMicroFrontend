package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.repository;

import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.entity.DetalleIngresoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Accede a datos JPA de detalles de ingreso
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface DetalleIngresoJpaRepository extends JpaRepository<DetalleIngresoJpaEntity, Integer> {

    List<DetalleIngresoJpaEntity> findAllByIngresoIdIngresoOrderByIdDetalleAsc(Integer idIngreso);
}
