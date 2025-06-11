// Enumeración de tipos de notificación
package com.hospital.backend.enums;

/**
 * Tipos de notificaciones en el sistema
 */
public enum NotificationType {
    APPOINTMENT_REMINDER("Recordatorio de cita"),
    APPOINTMENT_CONFIRMATION("Confirmación de cita"),
    APPOINTMENT_CANCELLED("Cita cancelada"),
    APPOINTMENT_RESCHEDULED("Cita reprogramada"),
    APPOINTMENT_COMPLETED("Cita completada"),
    
    PRESCRIPTION_CREATED("Receta creada"),
    PRESCRIPTION_UPDATED("Receta actualizada"),
    PRESCRIPTION_EXPIRING("Receta por expirar"),
    
    MEDICAL_RECORD_CREATED("Historial médico creado"),
    LAB_RESULTS_AVAILABLE("Resultados de laboratorio disponibles"),
    
    PAYMENT_RECEIVED("Pago recibido"),
    PAYMENT_DUE("Pago pendiente"),
    PAYMENT_OVERDUE("Pago vencido"),
    PAYMENT_REFUNDED("Pago reembolsado"),
    
    ACCOUNT_CREATED("Cuenta creada"),
    PASSWORD_RESET("Restablecer contraseña"),
    PASSWORD_CHANGED("Contraseña cambiada"),
    
    SYSTEM_ANNOUNCEMENT("Anuncio del sistema"),
    MAINTENANCE_SCHEDULED("Mantenimiento programado");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}