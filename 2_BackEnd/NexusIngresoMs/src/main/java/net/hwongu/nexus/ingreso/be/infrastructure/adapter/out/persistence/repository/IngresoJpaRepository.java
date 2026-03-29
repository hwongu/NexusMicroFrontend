package net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.repository;

import net.hwongu.nexus.ingreso.be.infrastructure.adapter.out.persistence.entity.IngresoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Accede a datos JPA de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface IngresoJpaRepository extends JpaRepository<IngresoJpaEntity, Integer> {

    List<IngresoJpaEntity> findAllByOrderByIdIngresoDesc();
}
