// Excepción para acceso no autorizado (401)
package com.hospital.backend.common.exception;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnauthorizedException() {
        super("Token de autenticación inválido o expirado");
    }
}