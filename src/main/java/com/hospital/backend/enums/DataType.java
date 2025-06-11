// Enumeración de tipos de dato para configuraciones
package com.hospital.backend.enums;

/**
 * Tipos de datos en el sistema
 */
public enum DataType {
    STRING("Texto"),
    INTEGER("Entero"),
    DECIMAL("Decimal"),
    BOOLEAN("Booleano"),
    DATE("Fecha"),
    TIME("Hora"),
    DATETIME("Fecha y hora"),
    JSON("JSON"),
    HTML("HTML"),
    MARKDOWN("Markdown"),
    XML("XML"),
    URL("URL"),
    EMAIL("Correo electrónico"),
    PHONE("Teléfono"),
    OBJECT_ID("ID de objeto");
    
    private final String displayName;
    
    DataType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}