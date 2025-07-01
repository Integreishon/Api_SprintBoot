package com.hospital.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for specialties
 */
@Configuration
@ConfigurationProperties(prefix = "app.specialty")
@Getter
@Setter
public class SpecialtyProperties {

    /**
     * Default ID for primary care specialty
     */
    private Long primary;
    
    /**
     * Default ID for referral specialty
     */
    private Long referral;
} 