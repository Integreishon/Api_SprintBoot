package com.hospital.backend.enums;

/**
 * Estados de derivaciones médicas
 */
public enum ReferralStatus {
    REQUESTED("Solicitada"),
    SCHEDULED("Programada"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada");
    
    private final String displayName;
    
    ReferralStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 