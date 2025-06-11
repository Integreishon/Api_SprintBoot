// Enumeración de estados de pago
package com.hospital.backend.enums;

public enum PaymentStatus {
    PENDING("Pendiente"),
    COMPLETED("Completado"),
    FAILED("Fallido"),
    REFUNDED("Reembolsado");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}