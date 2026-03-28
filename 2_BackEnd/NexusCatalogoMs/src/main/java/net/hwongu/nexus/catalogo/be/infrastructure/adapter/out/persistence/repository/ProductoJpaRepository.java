package net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.repository;

import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.entity.ProductoJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos JPA de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface ProductoJpaRepository extends JpaRepository<ProductoJpaEntity, Integer> {

    @EntityGraph(attributePaths = "categoria")
    List<ProductoJpaEntity> findAllByOrderByIdProductoAsc();

    @Override
    @EntityGraph(attributePaths = "categoria")
    Optional<ProductoJpaEntity> findById(Integer id);
}
