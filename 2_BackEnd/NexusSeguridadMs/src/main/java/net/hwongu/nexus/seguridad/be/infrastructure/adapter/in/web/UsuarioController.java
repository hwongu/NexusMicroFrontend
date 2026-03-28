package net.hwongu.nexus.seguridad.be.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hwongu.nexus.seguridad.be.domain.model.Usuario;
import net.hwongu.nexus.seguridad.be.domain.port.in.UsuarioUseCase;
import net.hwongu.nexus.seguridad.be.dto.LoginRequestDTO;
import net.hwongu.nexus.seguridad.be.dto.UsuarioDTO;
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
 * Expone endpoints REST de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioUseCase.listarUsuarios()
                        .stream()
                        .map(this::convertirADTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(convertirADTO(usuarioUseCase.buscarUsuarioPorId(id)));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuarioCreado = usuarioUseCase.registrarUsuario(convertirADominio(usuarioDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(usuarioCreado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> actualizarUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioDTO usuarioDTO
    ) {
        usuarioUseCase.actualizarUsuario(id, convertirADominio(usuarioDTO));
        return ResponseEntity.ok(Map.of("message", "Usuario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioUseCase.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(
                convertirADTO(
                        usuarioUseCase.autenticarUsuario(
                                loginRequestDTO.getUsername(),
                                loginRequestDTO.getPassword()
                        )
                )
        );
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .estado(usuario.getEstado())
                .build();
    }

    private Usuario convertirADominio(UsuarioDTO usuarioDTO) {
        return Usuario.builder()
                .idUsuario(usuarioDTO.getIdUsuario())
                .username(usuarioDTO.getUsername())
                .password(usuarioDTO.getPassword())
                .estado(usuarioDTO.getEstado())
                .build();
    }
}
