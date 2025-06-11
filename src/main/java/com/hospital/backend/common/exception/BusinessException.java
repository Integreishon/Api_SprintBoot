// Excepción para violaciones de reglas de negocio (422)
package com.hospital.backend.common.exception;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}