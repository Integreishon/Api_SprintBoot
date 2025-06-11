// Enumeración de tipos de operaciones para auditoría
package com.hospital.backend.enums;

/**
 * Tipos de operaciones registradas en el sistema de auditoría
 */
public enum OperationType {
    CREATE("Creación"),
    UPDATE("Actualización"),
    DELETE("Eliminación"),
    LOGIN("Inicio de sesión"),
    LOGOUT("Cierre de sesión"),
    ACCESS("Acceso"),
    EXPORT("Exportación"),
    IMPORT("Importación"),
    PAYMENT("Pago"),
    REFUND("Reembolso"),
    SYSTEM("Operación del sistema"),
    NOTIFICATION("Notificación"),
    PRINT("Impresión"),
    VIEW("Visualización"),
    OTHER("Otra operación");
    
    private final String displayName;
    
    OperationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 