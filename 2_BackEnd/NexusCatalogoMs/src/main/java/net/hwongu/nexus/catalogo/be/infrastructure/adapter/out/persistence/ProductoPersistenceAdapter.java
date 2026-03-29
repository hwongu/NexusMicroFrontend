package net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;
import net.hwongu.nexus.catalogo.be.domain.port.out.ProductoPersistencePort;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.entity.CategoriaJpaEntity;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.entity.ProductoJpaEntity;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.repository.CategoriaJpaRepository;
import net.hwongu.nexus.catalogo.be.infrastructure.adapter.out.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Accede a datos de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Component
@RequiredArgsConstructor
public class ProductoPersistenceAdapter implements ProductoPersistencePort {

    private final ProductoJpaRepository productoJpaRepository;
    private final CategoriaJpaRepository categoriaJpaRepository;

    @Override
    public List<Producto> findAllOrdered() {
        return productoJpaRepository.findAllByOrderByIdProductoAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Producto> findById(Integer id) {
        return productoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Producto save(Producto producto) {
        ProductoJpaEntity saved = productoJpaRepository.save(toEntity(producto));
        return toDomain(saved);
    }

    @Override
    public boolean existsById(Integer id) {
        return productoJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        productoJpaRepository.deleteById(id);
    }

    @Override
    public void flush() {
        productoJpaRepository.flush();
    }

    private Producto toDomain(ProductoJpaEntity entity) {
        return Producto.builder()
                .idProducto(entity.getIdProducto())
                .categoria(
                        Categoria.builder()
                                .idCategoria(entity.getCategoria().getIdCategoria())
                                .nombre(entity.getCategoria().getNombre())
                                .descripcion(entity.getCategoria().getDescripcion())
                                .build()
                )
                .nombre(entity.getNombre())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .build();
    }

    private ProductoJpaEntity toEntity(Producto producto) {
        CategoriaJpaEntity categoria = categoriaJpaRepository.getReferenceById(producto.getCategoria().getIdCategoria());

        return ProductoJpaEntity.builder()
                .idProducto(producto.getIdProducto())
                .categoria(categoria)
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .build();
    }
}
