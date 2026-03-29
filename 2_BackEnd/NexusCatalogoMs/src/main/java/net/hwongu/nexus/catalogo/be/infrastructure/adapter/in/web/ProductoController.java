package net.hwongu.nexus.catalogo.be.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.application.usecase.command.ActualizarStockCommand;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;
import net.hwongu.nexus.catalogo.be.domain.port.in.ProductoUseCase;
import net.hwongu.nexus.catalogo.be.dto.ActualizarStockRequestDTO;
import net.hwongu.nexus.catalogo.be.dto.ProductoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Expone endpoints REST de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoUseCase productoUseCase;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        return ResponseEntity.ok(
                productoUseCase.listarProductos()
                        .stream()
                        .map(this::convertirADTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarProductoPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(convertirADTO(productoUseCase.buscarProductoPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> registrarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Producto productoCreado = productoUseCase.registrarProducto(convertirADominio(productoDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(productoCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizarProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoDTO productoDTO
    ) {
        productoUseCase.actualizarProducto(id, convertirADominio(productoDTO));
        return ResponseEntity.ok(Map.of("message", "Producto actualizado exitosamente"));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, String>> actualizarStockProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarStockRequestDTO requestDTO
    ) {
        productoUseCase.actualizarStockProducto(
                id,
                new ActualizarStockCommand(requestDTO.getCantidad(), requestDTO.getOperacion())
        );
        return ResponseEntity.ok(Map.of("message", "Stock del producto actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        productoUseCase.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    private ProductoDTO convertirADTO(Producto producto) {
        return ProductoDTO.builder()
                .idProducto(producto.getIdProducto())
                .idCategoria(producto.getCategoria().getIdCategoria())
                .nombreCategoria(producto.getCategoria().getNombre())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .build();
    }

    private Producto convertirADominio(ProductoDTO productoDTO) {
        return Producto.builder()
                .idProducto(productoDTO.getIdProducto())
                .categoria(Categoria.builder().idCategoria(productoDTO.getIdCategoria()).build())
                .nombre(productoDTO.getNombre())
                .precio(productoDTO.getPrecio())
                .stock(productoDTO.getStock())
                .build();
    }
}
