package net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.repository;

import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.entity.CategoriaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Accede a datos JPA de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Integer> {

    List<CategoriaJpaEntity> findAllByOrderByIdCategoriaAsc();
}
