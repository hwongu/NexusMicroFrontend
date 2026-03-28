package net.hwongu.nexus.seguridad.be.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maneja errores globales del microservicio
 *
 * @author Henry Wong
 * GitHub @hwongu
 * https://github.com/hwongu
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarErroresDeValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        Map<String, String> errores = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        HttpStatus estadoHttp = HttpStatus.BAD_REQUEST;
        return construirRespuestaError(estadoHttp, errores, request.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> manejarBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        HttpStatus estadoHttp = HttpStatus.BAD_REQUEST;
        return construirRespuestaError(estadoHttp, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoAutorizado(
            NoAutorizadoException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        HttpStatus estadoHttp = HttpStatus.UNAUTHORIZED;
        return construirRespuestaError(estadoHttp, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        HttpStatus estadoHttp = HttpStatus.NOT_FOUND;
        return construirRespuestaError(estadoHttp, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> manejarErroresDeIntegridad(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        HttpStatus estadoHttp = HttpStatus.CONFLICT;
        return construirRespuestaError(estadoHttp, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorGenerico(
            Exception exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        HttpStatus estadoHttp = HttpStatus.INTERNAL_SERVER_ERROR;
        String mensaje = "Ocurrio un error interno no controlado en el servidor.";
        return construirRespuestaError(estadoHttp, mensaje, request.getRequestURI());
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuestaError(
            HttpStatus estadoHttp,
            Object mensaje,
            String ruta
    ) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(estadoHttp.value())
                .error(estadoHttp.getReasonPhrase())
                .message(mensaje)
                .path(ruta)
                .build();

        return ResponseEntity.status(estadoHttp).body(errorResponseDTO);
    }
}
