// Configuración de propiedades JWT para autenticación segura
package com.hospital.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private long expirationTime; // en milisegundos (se mapea desde expiration-time)
    private String issuer;
    private String tokenPrefix = "Bearer "; // se mapea desde token-prefix
    private String headerName = "Authorization"; // se mapea desde header-name
}