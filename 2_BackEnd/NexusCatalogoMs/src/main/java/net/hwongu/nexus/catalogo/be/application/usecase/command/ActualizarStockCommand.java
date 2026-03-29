package net.hwongu.nexus.catalogo.be.application.usecase.command;

/**
 * Agrupa los datos para actualizar stock
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
public record ActualizarStockCommand(Integer cantidad, String operacion) {
}
