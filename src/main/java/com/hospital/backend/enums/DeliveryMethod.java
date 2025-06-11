// Enumeración de métodos de entrega de notificaciones
package com.hospital.backend.enums;

/**
 * Métodos de entrega de notificaciones en el sistema
 */
public enum DeliveryMethod {
    PUSH("Notificación push"),
    EMAIL("Correo electrónico"),
    SMS("Mensaje de texto"),
    IN_APP("Notificación interna"),
    WHATSAPP("WhatsApp");
    
    private final String displayName;
    
    DeliveryMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}