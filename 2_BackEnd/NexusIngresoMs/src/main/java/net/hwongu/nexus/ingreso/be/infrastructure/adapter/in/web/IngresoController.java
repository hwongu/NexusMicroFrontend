package net.hwongu.nexus.ingreso.be.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.ingreso.be.domain.port.in.IngresoUseCase;
import net.hwongu.nexus.ingreso.be.dto.ActualizarEstadoIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.DetalleIngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.IngresoDTO;
import net.hwongu.nexus.ingreso.be.dto.RegistrarIngresoRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Expone endpoints REST de ingresos
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@RestController
@RequestMapping("/api/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoUseCase ingresoUseCase;

    @GetMapping
    public ResponseEntity<List<IngresoDTO>> listarIngresos() {
        return ResponseEntity.ok(ingresoUseCase.listarIngresos());
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleIngresoDTO>> buscarDetallesPorIngreso(@PathVariable Integer id) {
        return ResponseEntity.ok(ingresoUseCase.buscarDetallesPorIngreso(id));
    }

    @PostMapping
    public ResponseEntity<IngresoDTO> registrarIngreso(@Valid @RequestBody RegistrarIngresoRequestDTO requestDTO) {
        IngresoDTO ingresoCreado = ingresoUseCase.registrarIngresoCompleto(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingresoCreado);
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<Map<String, String>> anularIngreso(@PathVariable Integer id) {
        ingresoUseCase.anularIngreso(id);
        return ResponseEntity.ok(Map.of("message", "Ingreso anulado exitosamente"));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Map<String, String>> actualizarEstadoIngreso(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoIngresoDTO requestDTO
    ) {
        ingresoUseCase.actualizarEstadoIngreso(id, requestDTO.getEstado());
        return ResponseEntity.ok(Map.of("message", "Estado del ingreso actualizado correctamente"));
    }
}
