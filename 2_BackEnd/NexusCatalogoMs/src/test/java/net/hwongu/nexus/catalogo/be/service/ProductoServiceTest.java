package net.hwongu.nexus.catalogo.be.service;

import net.hwongu.nexus.catalogo.be.application.usecase.ProductoUseCaseImpl;
import net.hwongu.nexus.catalogo.be.application.usecase.command.ActualizarStockCommand;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.model.Producto;
import net.hwongu.nexus.catalogo.be.domain.port.out.CategoriaPersistencePort;
import net.hwongu.nexus.catalogo.be.domain.port.out.ProductoPersistencePort;
import net.hwongu.nexus.catalogo.be.exception.BadRequestException;
import net.hwongu.nexus.catalogo.be.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifica los casos de uso de productos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class ProductoUseCaseImplTest {

    @Mock
    private ProductoPersistencePort productoPersistencePort;

    @Mock
    private CategoriaPersistencePort categoriaPersistencePort;

    @InjectMocks
    private ProductoUseCaseImpl productoUseCase;

    @Test
    void listarProductos_debeRetornarProductosDelDominio() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto producto1 = crearProducto(1, categoria, "ThinkPad T14", 1200.5, 50);
        Producto producto2 = crearProducto(2, categoria, "ThinkPad X1", 1800.0, 20);
        when(productoPersistencePort.findAllOrdered()).thenReturn(List.of(producto1, producto2));

        List<Producto> resultado = productoUseCase.listarProductos();

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getIdProducto());
        assertEquals("Laptops", resultado.get(0).getCategoria().getNombre());
        assertEquals("ThinkPad X1", resultado.get(1).getNombre());
        verify(productoPersistencePort).findAllOrdered();
    }

    @Test
    void listarProductos_debeRetornarListaVaciaCuandoNoHayRegistros() {
        when(productoPersistencePort.findAllOrdered()).thenReturn(List.of());

        List<Producto> resultado = productoUseCase.listarProductos();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(productoPersistencePort).findAllOrdered();
    }

    @Test
    void buscarProductoPorId_debeRetornarProductoCuandoExiste() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto producto = crearProducto(7, categoria, "ThinkPad T14", 1200.5, 50);
        when(productoPersistencePort.findById(7)).thenReturn(Optional.of(producto));

        Producto resultado = productoUseCase.buscarProductoPorId(7);

        assertEquals(7, resultado.getIdProducto());
        assertEquals(1, resultado.getCategoria().getIdCategoria());
        assertEquals("Laptops", resultado.getCategoria().getNombre());
        assertEquals("ThinkPad T14", resultado.getNombre());
        verify(productoPersistencePort).findById(7);
    }

    @Test
    void buscarProductoPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(productoPersistencePort.findById(99)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoUseCase.buscarProductoPorId(99)
        );

        assertEquals("Producto no encontrado.", exception.getMessage());
        verify(productoPersistencePort).findById(99);
    }

    @Test
    void registrarProducto_debeGuardarProductoCuandoCategoriaExiste() {
        Producto request = Producto.builder()
                .idProducto(40)
                .categoria(Categoria.builder().idCategoria(1).build())
                .nombre("ThinkPad T14")
                .precio(1200.5)
                .stock(50)
                .build();
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto productoGuardado = crearProducto(5, categoria, "ThinkPad T14", 1200.5, 50);
        when(categoriaPersistencePort.findById(1)).thenReturn(Optional.of(categoria));
        when(productoPersistencePort.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = productoUseCase.registrarProducto(request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoPersistencePort).save(captor.capture());
        Producto productoEnviadoAGuardar = captor.getValue();

        assertNull(productoEnviadoAGuardar.getIdProducto());
        assertEquals("ThinkPad T14", productoEnviadoAGuardar.getNombre());
        assertEquals(categoria, productoEnviadoAGuardar.getCategoria());
        assertEquals(5, resultado.getIdProducto());
        assertEquals("Laptops", resultado.getCategoria().getNombre());
    }

    @Test
    void registrarProducto_debeLanzarBadRequestCuandoCategoriaNoExiste() {
        Producto request = Producto.builder()
                .categoria(Categoria.builder().idCategoria(99).build())
                .nombre("ThinkPad T14")
                .precio(1200.5)
                .stock(50)
                .build();
        when(categoriaPersistencePort.findById(99)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoUseCase.registrarProducto(request)
        );

        assertEquals("La categoria indicada no existe.", exception.getMessage());
        verify(categoriaPersistencePort).findById(99);
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeActualizarDatosCuandoProductoYCategoriaExisten() {
        Categoria categoriaActual = crearCategoria(1, "Laptops");
        Categoria categoriaNueva = crearCategoria(2, "Monitores");
        Producto productoExistente = crearProducto(3, categoriaActual, "ThinkPad T14", 1200.5, 50);
        Producto request = Producto.builder()
                .categoria(Categoria.builder().idCategoria(2).build())
                .nombre("Monitor Ultrawide")
                .precio(999.0)
                .stock(15)
                .build();
        when(productoPersistencePort.findById(3)).thenReturn(Optional.of(productoExistente));
        when(categoriaPersistencePort.findById(2)).thenReturn(Optional.of(categoriaNueva));

        productoUseCase.actualizarProducto(3, request);

        verify(productoPersistencePort).save(productoExistente);
        assertEquals(categoriaNueva, productoExistente.getCategoria());
        assertEquals("Monitor Ultrawide", productoExistente.getNombre());
        assertEquals(999.0, productoExistente.getPrecio());
        assertEquals(15, productoExistente.getStock());
    }

    @Test
    void actualizarProducto_debeLanzarExcepcionCuandoProductoNoExiste() {
        Producto request = Producto.builder()
                .categoria(Categoria.builder().idCategoria(1).build())
                .nombre("Monitor Ultrawide")
                .precio(999.0)
                .stock(15)
                .build();
        when(productoPersistencePort.findById(3)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoUseCase.actualizarProducto(3, request)
        );

        assertEquals("Producto no encontrado.", exception.getMessage());
        verify(productoPersistencePort).findById(3);
        verify(categoriaPersistencePort, never()).findById(org.mockito.ArgumentMatchers.anyInt());
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_debeLanzarBadRequestCuandoCategoriaNoExiste() {
        Categoria categoriaActual = crearCategoria(1, "Laptops");
        Producto productoExistente = crearProducto(3, categoriaActual, "ThinkPad T14", 1200.5, 50);
        Producto request = Producto.builder()
                .categoria(Categoria.builder().idCategoria(9).build())
                .nombre("Monitor Ultrawide")
                .precio(999.0)
                .stock(15)
                .build();
        when(productoPersistencePort.findById(3)).thenReturn(Optional.of(productoExistente));
        when(categoriaPersistencePort.findById(9)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoUseCase.actualizarProducto(3, request)
        );

        assertEquals("La categoria indicada no existe.", exception.getMessage());
        verify(productoPersistencePort).findById(3);
        verify(categoriaPersistencePort).findById(9);
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void actualizarStockProducto_debeSumarStockCuandoOperacionEsSumar() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto productoExistente = crearProducto(8, categoria, "ThinkPad T14", 1200.5, 50);
        ActualizarStockCommand command = new ActualizarStockCommand(10, " sumar ");
        when(productoPersistencePort.findById(8)).thenReturn(Optional.of(productoExistente));

        productoUseCase.actualizarStockProducto(8, command);

        assertEquals(60, productoExistente.getStock());
        verify(productoPersistencePort).save(productoExistente);
    }

    @Test
    void actualizarStockProducto_debeRestarStockCuandoOperacionEsRestar() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto productoExistente = crearProducto(8, categoria, "ThinkPad T14", 1200.5, 50);
        ActualizarStockCommand command = new ActualizarStockCommand(5, "RESTAR");
        when(productoPersistencePort.findById(8)).thenReturn(Optional.of(productoExistente));

        productoUseCase.actualizarStockProducto(8, command);

        assertEquals(45, productoExistente.getStock());
        verify(productoPersistencePort).save(productoExistente);
    }

    @Test
    void actualizarStockProducto_debeLanzarExcepcionCuandoProductoNoExiste() {
        ActualizarStockCommand command = new ActualizarStockCommand(5, "SUMAR");
        when(productoPersistencePort.findById(8)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoUseCase.actualizarStockProducto(8, command)
        );

        assertEquals("Producto no encontrado.", exception.getMessage());
        verify(productoPersistencePort).findById(8);
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void actualizarStockProducto_debeLanzarBadRequestCuandoOperacionEsInvalida() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto productoExistente = crearProducto(8, categoria, "ThinkPad T14", 1200.5, 50);
        ActualizarStockCommand command = new ActualizarStockCommand(5, "DIVIDIR");
        when(productoPersistencePort.findById(8)).thenReturn(Optional.of(productoExistente));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productoUseCase.actualizarStockProducto(8, command)
        );

        assertEquals("La operacion debe ser SUMAR o RESTAR.", exception.getMessage());
        verify(productoPersistencePort).findById(8);
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void actualizarStockProducto_debeLanzarErrorCuandoStockQuedaNegativo() {
        Categoria categoria = crearCategoria(1, "Laptops");
        Producto productoExistente = crearProducto(8, categoria, "ThinkPad T14", 1200.5, 3);
        ActualizarStockCommand command = new ActualizarStockCommand(5, "RESTAR");
        when(productoPersistencePort.findById(8)).thenReturn(Optional.of(productoExistente));

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> productoUseCase.actualizarStockProducto(8, command)
        );

        assertEquals(
                "No se puede restar el stock porque el producto quedaria con stock negativo.",
                exception.getMessage()
        );
        verify(productoPersistencePort).findById(8);
        verify(productoPersistencePort, never()).save(any(Producto.class));
    }

    @Test
    void eliminarProducto_debeEliminarYHacerFlushCuandoExiste() {
        when(productoPersistencePort.existsById(4)).thenReturn(true);

        productoUseCase.eliminarProducto(4);

        verify(productoPersistencePort).existsById(4);
        verify(productoPersistencePort).deleteById(4);
        verify(productoPersistencePort).flush();
    }

    @Test
    void eliminarProducto_debeLanzarExcepcionCuandoNoExiste() {
        when(productoPersistencePort.existsById(12)).thenReturn(false);

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> productoUseCase.eliminarProducto(12)
        );

        assertEquals("Producto no encontrado.", exception.getMessage());
        verify(productoPersistencePort).existsById(12);
        verify(productoPersistencePort, never()).deleteById(12);
        verify(productoPersistencePort, never()).flush();
    }

    @Test
    void eliminarProducto_debeTraducirErrorDeIntegridadReferencial() {
        when(productoPersistencePort.existsById(4)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("fk")).when(productoPersistencePort).flush();

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> productoUseCase.eliminarProducto(4)
        );

        assertEquals(
                "No se puede eliminar el producto porque esta referenciado en un ingreso.",
                exception.getMessage()
        );
        verify(productoPersistencePort).deleteById(4);
        verify(productoPersistencePort).flush();
    }

    private Categoria crearCategoria(Integer id, String nombre) {
        return Categoria.builder()
                .idCategoria(id)
                .nombre(nombre)
                .descripcion("Descripcion")
                .build();
    }

    private Producto crearProducto(Integer id, Categoria categoria, String nombre, Double precio, Integer stock) {
        return Producto.builder()
                .idProducto(id)
                .categoria(categoria)
                .nombre(nombre)
                .precio(precio)
                .stock(stock)
                .build();
    }
}
