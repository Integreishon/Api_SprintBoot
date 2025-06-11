// Enumeración de tipos de archivo médico
package com.hospital.backend.enums;

/**
 * Tipos de archivos en el sistema
 */
public enum FileType {
    PDF("Archivo PDF"),
    IMAGE("Imagen"),
    DOCUMENT("Documento"),
    LAB_RESULT("Resultado de laboratorio"),
    X_RAY("Radiografía"),
    ULTRASOUND("Ecografía"),
    MRI("Resonancia magnética"),
    CT_SCAN("Tomografía computarizada"),
    OTHER("Otro");
    
    private final String displayName;
    
    FileType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}