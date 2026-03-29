package net.hwongu.nexus.ingreso.be.domain.port.out;

import net.hwongu.nexus.ingreso.be.dto.UsuarioRemotoDTO;

/**
 * Define la consulta remota de usuarios
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public interface UsuarioRemotoPort {

    UsuarioRemotoDTO validarUsuarioActivo(Integer idUsuario);

    UsuarioRemotoDTO buscarUsuario(Integer idUsuario);
}
