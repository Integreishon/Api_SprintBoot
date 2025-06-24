package com.hospital.backend.enums;

/**
 * Tipos de notificaciones para el sistema de correo electrónico
 * NOTA: Esta clase se mantiene por compatibilidad con el servicio de correo,
 * pero las notificaciones como entidad han sido eliminadas.
 * Adaptado a la nueva lógica de Urovital (sin prescripciones)
 */
public enum NotificationType {
    APPOINTMENT_REMINDER("Recordatorio de Cita"),
    APPOINTMENT_CONFIRMATION("Confirmación de Cita"),
    APPOINTMENT_CANCELLED("Cita Cancelada"),
    APPOINTMENT_RESCHEDULED("Cita Reprogramada"),
    PASSWORD_RESET("Restablecimiento de Contraseña"),
    LAB_RESULTS_AVAILABLE("Resultados de Laboratorio Disponibles"),
    PAYMENT_RECEIVED("Pago Recibido"),
    PAYMENT_DUE("Pago Pendiente");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 