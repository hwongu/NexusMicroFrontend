package net.hwongu.nexus.catalogo.be.domain.port.in;

import net.hwongu.nexus.catalogo.be.application.usecase.command.ActualizarStockCommand;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;

import java.util.List;

/**
 * Define los casos de uso de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface ProductoUseCase {

    List<Producto> listarProductos();

    Producto buscarProductoPorId(Integer id);

    Producto registrarProducto(Producto producto);

    void actualizarProducto(Integer id, Producto producto);

    void actualizarStockProducto(Integer id, ActualizarStockCommand command);

    void eliminarProducto(Integer id);
}
