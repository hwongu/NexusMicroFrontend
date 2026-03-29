package net.hwongu.nexus.ingreso.be.exception;

/**
 * Representa un fallo de integracion remota
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public class IntegracionRemotaException extends RuntimeException {

    public IntegracionRemotaException(String message) {
        super(message);
    }
}
