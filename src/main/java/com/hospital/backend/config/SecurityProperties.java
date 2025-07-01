package com.hospital.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for security
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {

    /**
     * JWT security configuration
     */
    private Jwt jwt = new Jwt();
    
    @Getter
    @Setter
    public static class Jwt {
        /**
         * Secret key for JWT signing
         */
        private String secret;
        
        /**
         * Expiration time in milliseconds
         */
        private Long expiration;
    }
} 