package com.hospital.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for payments
 */
@Configuration
@ConfigurationProperties(prefix = "app.payment")
@Getter
@Setter
public class PaymentProperties {

    /**
     * Whether payment validation is required
     */
    private Boolean validationRequired = true;
    
    /**
     * Payment timeout in minutes
     */
    private Integer timeoutMinutes = 30;
} 