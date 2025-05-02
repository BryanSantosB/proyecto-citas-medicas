package com.citasmedicas.spring.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de recurso no encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "error", "Recurso no encontrado",
                        "mensaje", ex.getMessage()
                )
        );
    }

    // Manejo de enums inválidos en JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEnum(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", "Valor inválido para enum",
                            "mensaje", "Valores válidos: " + Arrays.toString(ife.getTargetType().getEnumConstants())
                    )
            );
        }

        // Otro error de deserialización
        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", "Error en el cuerpo de la solicitud",
                        "mensaje", ex.getMessage()
                )
        );
    }

    // Manejo genérico de errores no controlados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "error", "Error interno",
                        "mensaje", ex.getMessage()
                )
        );
    }
}