package net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.port.out.CategoriaPersistencePort;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.entity.CategoriaJpaEntity;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.repository.CategoriaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
@RequiredArgsConstructor
public class CategoriaPersistenceAdapter implements CategoriaPersistencePort {

    private final CategoriaJpaRepository categoriaJpaRepository;

    @Override
    public List<Categoria> findAllOrdered() {
        return categoriaJpaRepository.findAllByOrderByIdCategoriaAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Categoria> findById(Integer id) {
        return categoriaJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaJpaEntity saved = categoriaJpaRepository.save(toEntity(categoria));
        return toDomain(saved);
    }

    @Override
    public boolean existsById(Integer id) {
        return categoriaJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        categoriaJpaRepository.deleteById(id);
    }

    @Override
    public void flush() {
        categoriaJpaRepository.flush();
    }

    private Categoria toDomain(CategoriaJpaEntity entity) {
        return Categoria.builder()
                .idCategoria(entity.getIdCategoria())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    private CategoriaJpaEntity toEntity(Categoria categoria) {
        return CategoriaJpaEntity.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}
