package com.hospital.backend.enums;

/**
 * Tipos de doctores en el sistema
 */
public enum DoctorType {
    PRIMARY("Médico Principal"),
    SPECIALIST("Especialista");
    
    private final String displayName;
    
    DoctorType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 