package net.hwongu.nexus.seguridad.be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transporta credenciales de autenticacion
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "El username es obligatorio.")
    private String username;

    @NotBlank(message = "El password es obligatorio.")
    private String password;
}
