package net.hwongu.nexus.catalogo.be.service;

import net.hwongu.nexus.catalogo.be.application.usecase.CategoriaUseCaseImpl;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.port.out.CategoriaPersistencePort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifica los casos de uso de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@ExtendWith(MockitoExtension.class)
class CategoriaUseCaseImplTest {

    @Mock
    private CategoriaPersistencePort categoriaPersistencePort;

    @InjectMocks
    private CategoriaUseCaseImpl categoriaUseCase;

    @Test
    void listarCategorias_debeRetornarCategoriasDelDominio() {
        Categoria categoria1 = crearCategoria(1, "Laptops", "Equipos portatiles");
        Categoria categoria2 = crearCategoria(2, "Monitores", "Pantallas");
        when(categoriaPersistencePort.findAllOrdered()).thenReturn(List.of(categoria1, categoria2));

        List<Categoria> resultado = categoriaUseCase.listarCategorias();

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getIdCategoria());
        assertEquals("Laptops", resultado.get(0).getNombre());
        assertEquals(2, resultado.get(1).getIdCategoria());
        assertEquals("Monitores", resultado.get(1).getNombre());
        verify(categoriaPersistencePort).findAllOrdered();
    }

    @Test
    void listarCategorias_debeRetornarListaVaciaCuandoNoHayRegistros() {
        when(categoriaPersistencePort.findAllOrdered()).thenReturn(List.of());

        List<Categoria> resultado = categoriaUseCase.listarCategorias();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
        verify(categoriaPersistencePort).findAllOrdered();
    }

    @Test
    void buscarCategoriaPorId_debeRetornarCategoriaCuandoExiste() {
        Categoria categoria = crearCategoria(1, "Laptops", "Equipos portatiles");
        when(categoriaPersistencePort.findById(1)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaUseCase.buscarCategoriaPorId(1);

        assertEquals(1, resultado.getIdCategoria());
        assertEquals("Laptops", resultado.getNombre());
        assertEquals("Equipos portatiles", resultado.getDescripcion());
        verify(categoriaPersistencePort).findById(1);
    }

    @Test
    void buscarCategoriaPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(categoriaPersistencePort.findById(99)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> categoriaUseCase.buscarCategoriaPorId(99)
        );

        assertEquals("Categoria no encontrada.", exception.getMessage());
        verify(categoriaPersistencePort).findById(99);
    }

    @Test
    void registrarCategoria_debeGuardarCategoriaConIdNuloYRetornarDominio() {
        Categoria request = crearCategoria(88, "Perifericos", "Mouse y teclados");
        Categoria categoriaGuardada = crearCategoria(5, "Perifericos", "Mouse y teclados");
        when(categoriaPersistencePort.save(org.mockito.ArgumentMatchers.any(Categoria.class))).thenReturn(categoriaGuardada);

        Categoria resultado = categoriaUseCase.registrarCategoria(request);

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaPersistencePort).save(captor.capture());
        Categoria categoriaEnviadaAGuardar = captor.getValue();

        assertNull(categoriaEnviadaAGuardar.getIdCategoria());
        assertEquals("Perifericos", categoriaEnviadaAGuardar.getNombre());
        assertEquals("Mouse y teclados", categoriaEnviadaAGuardar.getDescripcion());
        assertEquals(5, resultado.getIdCategoria());
        assertEquals("Perifericos", resultado.getNombre());
    }

    @Test
    void actualizarCategoria_debeActualizarDatosCuandoExiste() {
        Categoria categoriaExistente = crearCategoria(3, "Monitores", "Pantallas");
        Categoria request = crearCategoria(null, "Monitores Gamer", "Pantallas de alta frecuencia");
        when(categoriaPersistencePort.findById(3)).thenReturn(Optional.of(categoriaExistente));

        categoriaUseCase.actualizarCategoria(3, request);

        verify(categoriaPersistencePort).findById(3);
        verify(categoriaPersistencePort).save(categoriaExistente);
        assertEquals("Monitores Gamer", categoriaExistente.getNombre());
        assertEquals("Pantallas de alta frecuencia", categoriaExistente.getDescripcion());
    }

    @Test
    void actualizarCategoria_debeLanzarExcepcionCuandoNoExiste() {
        Categoria request = crearCategoria(null, "Monitores Gamer", "Pantallas de alta frecuencia");
        when(categoriaPersistencePort.findById(7)).thenReturn(Optional.empty());

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> categoriaUseCase.actualizarCategoria(7, request)
        );

        assertEquals("Categoria no encontrada.", exception.getMessage());
        verify(categoriaPersistencePort).findById(7);
        verify(categoriaPersistencePort, never()).save(org.mockito.ArgumentMatchers.any(Categoria.class));
    }

    @Test
    void eliminarCategoria_debeEliminarYHacerFlushCuandoExiste() {
        when(categoriaPersistencePort.existsById(4)).thenReturn(true);

        categoriaUseCase.eliminarCategoria(4);

        verify(categoriaPersistencePort).existsById(4);
        verify(categoriaPersistencePort).deleteById(4);
        verify(categoriaPersistencePort).flush();
    }

    @Test
    void eliminarCategoria_debeLanzarExcepcionCuandoNoExiste() {
        when(categoriaPersistencePort.existsById(10)).thenReturn(false);

        RecursoNoEncontradoException exception = assertThrows(
                RecursoNoEncontradoException.class,
                () -> categoriaUseCase.eliminarCategoria(10)
        );

        assertEquals("Categoria no encontrada.", exception.getMessage());
        verify(categoriaPersistencePort).existsById(10);
        verify(categoriaPersistencePort, never()).deleteById(10);
        verify(categoriaPersistencePort, never()).flush();
    }

    @Test
    void eliminarCategoria_debeTraducirErrorDeIntegridadReferencial() {
        when(categoriaPersistencePort.existsById(4)).thenReturn(true);
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("fk"))
                .when(categoriaPersistencePort).flush();

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> categoriaUseCase.eliminarCategoria(4)
        );

        assertEquals(
                "No se puede eliminar la categoria porque tiene productos asociados.",
                exception.getMessage()
        );
        verify(categoriaPersistencePort).deleteById(4);
        verify(categoriaPersistencePort).flush();
    }

    private Categoria crearCategoria(Integer id, String nombre, String descripcion) {
        return Categoria.builder()
                .idCategoria(id)
                .nombre(nombre)
                .descripcion(descripcion)
                .build();
    }
}
