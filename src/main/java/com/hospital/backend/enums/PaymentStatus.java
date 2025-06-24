// Enumeración de estados de pago
package com.hospital.backend.enums;

/**
 * Estados de pago en el sistema
 * NOTA: Solo se crea cita cuando el pago está COMPLETED
 */
public enum PaymentStatus {
    PROCESSING("En proceso"), // Validando comprobante o procesando tarjeta
    COMPLETED("Completado"), // Pago confirmado -> se crea appointment
    FAILED("Fallido"),       // Pago falló -> usuario intenta de nuevo
    REFUNDED("Reembolsado"); // Para cancelaciones con devolución

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}