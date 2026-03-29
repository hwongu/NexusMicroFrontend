package net.hwongu.nexus.catalogo.be.application.usecase;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.application.usecase.command.ActualizarStockCommand;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;
import net.hwongu.nexus.catalogo.be.domain.port.in.ProductoUseCase;
import net.hwongu.nexus.catalogo.be.domain.port.out.CategoriaPersistencePort;
import net.hwongu.nexus.catalogo.be.domain.port.out.ProductoPersistencePort;
import net.hwongu.nexus.catalogo.be.exception.BadRequestException;
import net.hwongu.nexus.catalogo.be.exception.RecursoNoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina los casos de uso de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Service
@RequiredArgsConstructor
public class ProductoUseCaseImpl implements ProductoUseCase {

    private final ProductoPersistencePort productoPersistencePort;
    private final CategoriaPersistencePort categoriaPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoPersistencePort.findAllOrdered();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarProductoPorId(Integer id) {
        return productoPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));
    }

    @Override
    @Transactional
    public Producto registrarProducto(Producto producto) {
        Categoria categoria = obtenerCategoriaExistente(producto.getCategoria().getIdCategoria());
        producto.setIdProducto(null);
        producto.setCategoria(categoria);
        return productoPersistencePort.save(producto);
    }

    @Override
    @Transactional
    public void actualizarProducto(Integer id, Producto producto) {
        Producto productoExistente = productoPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));

        Categoria categoria = obtenerCategoriaExistente(producto.getCategoria().getIdCategoria());

        productoExistente.setCategoria(categoria);
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setStock(producto.getStock());

        productoPersistencePort.save(productoExistente);
    }

    @Override
    @Transactional
    public void actualizarStockProducto(Integer id, ActualizarStockCommand command) {
        Producto productoExistente = productoPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));

        String operacion = command.operacion().trim().toUpperCase();
        Integer cantidad = command.cantidad();

        if (!"SUMAR".equals(operacion) && !"RESTAR".equals(operacion)) {
            throw new BadRequestException("La operacion debe ser SUMAR o RESTAR.");
        }

        if ("SUMAR".equals(operacion)) {
            productoExistente.setStock(productoExistente.getStock() + cantidad);
        } else {
            int nuevoStock = productoExistente.getStock() - cantidad;

            if (nuevoStock < 0) {
                throw new DataIntegrityViolationException(
                        "No se puede restar el stock porque el producto quedaria con stock negativo."
                );
            }

            productoExistente.setStock(nuevoStock);
        }

        productoPersistencePort.save(productoExistente);
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer id) {
        if (!productoPersistencePort.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto no encontrado.");
        }

        try {
            productoPersistencePort.deleteById(id);
            productoPersistencePort.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    "No se puede eliminar el producto porque esta referenciado en un ingreso.",
                    e
            );
        }
    }

    private Categoria obtenerCategoriaExistente(Integer idCategoria) {
        return categoriaPersistencePort.findById(idCategoria)
                .orElseThrow(() -> new BadRequestException("La categoria indicada no existe."));
    }
}
