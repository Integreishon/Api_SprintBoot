package com.hospital.backend.enums;

/**
 * Niveles de severidad para registros médicos
 */
public enum SeverityLevel {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Crítica");
    
    private final String displayName;
    
    SeverityLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 