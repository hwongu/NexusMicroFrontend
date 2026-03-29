package net.hwongu.nexus.ingreso.be.domain.port.out;

import net.hwongu.nexus.ingreso.be.dto.ProductoRemotoDTO;

/**
 * Define la consulta remota de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface ProductoRemotoPort {

    ProductoRemotoDTO validarProductoExistente(Integer idProducto);

    ProductoRemotoDTO buscarProducto(Integer idProducto);
}
