// Enumeración de tipos de método de pago
package com.hospital.backend.enums;

/**
 * Enumeration for payment method types in the hospital system
 */
public enum PaymentMethodType {
    CASH("Efectivo"),
    CREDIT_CARD("Tarjeta de Crédito"),
    DEBIT_CARD("Tarjeta de Débito"),
    BANK_TRANSFER("Transferencia Bancaria"),
    INSURANCE("Seguro Médico"),
    ONLINE("Pago en Línea"),
    OTHER("Otro");
    
    private final String displayName;
    
    PaymentMethodType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}