// Enumeración de tipos de método de pago
package com.hospital.backend.enums;

/**
 * Tipos de métodos de pago en el sistema
 * Adaptados específicamente para Centro Médico Urovital
 */
public enum PaymentMethodType {
    CASH("Efectivo"),
    DIGITAL("Digital"), // Para Yape/Plin
    CARD("Tarjeta");   // Tarjeta de crédito/débito
    
    private final String displayName;
    
    PaymentMethodType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}