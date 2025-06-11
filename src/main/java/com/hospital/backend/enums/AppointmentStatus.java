// Enumeración de estados de citas médicas
package com.hospital.backend.enums;

/**
 * Estados de una cita médica en el sistema
 */
public enum AppointmentStatus {
    SCHEDULED("Programada"),
    CONFIRMED("Confirmada"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    NO_SHOW("No asistió"),
    RESCHEDULED("Reprogramada");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}