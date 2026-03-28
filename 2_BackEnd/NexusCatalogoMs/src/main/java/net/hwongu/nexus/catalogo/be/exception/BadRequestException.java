package net.hwongu.nexus.catalogo.be.exception;

/**
 * Representa una solicitud invalida
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
