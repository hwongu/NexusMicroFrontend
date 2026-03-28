package net.hwongu.nexus.catalogo.be.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.catalogo.be.domain.model.Categoria;
import net.hwongu.nexus.catalogo.be.domain.port.in.CategoriaUseCase;
import net.hwongu.nexus.catalogo.be.dto.CategoriaDTO;
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
 * Expone endpoints REST de categorias
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaUseCase categoriaUseCase;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        return ResponseEntity.ok(
                categoriaUseCase.listarCategorias()
                        .stream()
                        .map(this::convertirADTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarCategoriaPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(convertirADTO(categoriaUseCase.buscarCategoriaPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> registrarCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoriaCreada = categoriaUseCase.registrarCategoria(convertirADominio(categoriaDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(categoriaCreada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizarCategoria(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaDTO categoriaDTO
    ) {
        categoriaUseCase.actualizarCategoria(id, convertirADominio(categoriaDTO));
        return ResponseEntity.ok(Map.of("message", "Categoria actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        categoriaUseCase.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }

    private Categoria convertirADominio(CategoriaDTO categoriaDTO) {
        return Categoria.builder()
                .idCategoria(categoriaDTO.getIdCategoria())
                .nombre(categoriaDTO.getNombre())
                .descripcion(categoriaDTO.getDescripcion())
                .build();
    }
}
