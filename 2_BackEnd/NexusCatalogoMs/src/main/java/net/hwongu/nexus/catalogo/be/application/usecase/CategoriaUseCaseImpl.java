package net.hwongu.nexus.catalogo.be.application.usecase;

import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.port.in.CategoriaUseCase;
import net.hwongu.nexus.catalogo.be.domain.port.out.CategoriaPersistencePort;
import net.hwongu.nexus.catalogo.be.exception.RecursoNoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Coordina los casos de uso de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Service
@RequiredArgsConstructor
public class CategoriaUseCaseImpl implements CategoriaUseCase {

    private final CategoriaPersistencePort categoriaPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaPersistencePort.findAllOrdered();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarCategoriaPorId(Integer id) {
        return categoriaPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada."));
    }

    @Override
    @Transactional
    public Categoria registrarCategoria(Categoria categoria) {
        categoria.setIdCategoria(null);
        return categoriaPersistencePort.save(categoria);
    }

    @Override
    @Transactional
    public void actualizarCategoria(Integer id, Categoria categoria) {
        Categoria categoriaExistente = categoriaPersistencePort.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada."));

        categoriaExistente.setNombre(categoria.getNombre());
        categoriaExistente.setDescripcion(categoria.getDescripcion());

        categoriaPersistencePort.save(categoriaExistente);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Integer id) {
        if (!categoriaPersistencePort.existsById(id)) {
            throw new RecursoNoEncontradoException("Categoria no encontrada.");
        }

        try {
            categoriaPersistencePort.deleteById(id);
            categoriaPersistencePort.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    "No se puede eliminar la categoria porque tiene productos asociados.",
                    e
            );
        }
    }
}
