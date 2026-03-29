package net.hwongu.nexus.seguridad.be.exception;

/**
 * Representa un acceso no autorizado
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public class NoAutorizadoException extends RuntimeException {

    public NoAutorizadoException(String message) {
        super(message);
    }
}
