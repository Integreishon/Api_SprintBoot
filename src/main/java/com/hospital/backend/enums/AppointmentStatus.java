// Enumeración de estados de citas médicas
package com.hospital.backend.enums;

/**
 * Estados de una cita médica en el sistema
 * NOTA: Todas las citas requieren pago confirmado para ser creadas
 */
public enum AppointmentStatus {
    PENDING_VALIDATION("Pendiente de validación"),
    SCHEDULED("Programada"), // Solo existe si ya está pagada
    IN_CONSULTATION("En consulta"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    NO_SHOW("No asistió");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}