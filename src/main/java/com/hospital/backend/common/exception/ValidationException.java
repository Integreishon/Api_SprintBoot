// Excepción para errores de validación de datos (400)
package com.hospital.backend.common.exception;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String value, String reason) {
        super(String.format("Validación fallida para campo '%s' con valor '%s': %s", 
            field, value, reason));
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}