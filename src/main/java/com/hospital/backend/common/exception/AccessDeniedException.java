package com.hospital.backend.common.exception;

/**
 * Excepción para manejar errores de acceso denegado
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
} 