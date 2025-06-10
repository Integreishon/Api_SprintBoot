// Configuración de propiedades JWT para autenticación segura
package com.hospital.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    private String secret = "hospitalSecretKey2024SuperSecureKeyForJWTTokenGenerationAndValidation";
    private long expiration = 86400000; // 24 horas en milisegundos
    private String issuer = "hospital-api";
    private String tokenPrefix = "Bearer ";
    private String headerName = "Authorization";
    
}