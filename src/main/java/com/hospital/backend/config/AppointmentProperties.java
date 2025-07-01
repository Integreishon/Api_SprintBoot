package com.hospital.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for appointments
 */
@Configuration
@ConfigurationProperties(prefix = "app.appointment")
@Getter
@Setter
public class AppointmentProperties {

    private TimeBlock morning = new TimeBlock();
    private TimeBlock afternoon = new TimeBlock();
    
    @Getter
    @Setter
    public static class TimeBlock {
        private String start;
        private String end;
        private Integer capacity;
    }
} 