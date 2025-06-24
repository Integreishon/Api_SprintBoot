// Enumeración de tipos de archivos
package com.hospital.backend.enums;

/**
 * Tipos de archivos que pueden ser subidos al sistema
 */
public enum FileType {
    PDF("Documento PDF"),
    IMAGE("Imagen"),
    DOCUMENT("Documento"),
    PRESCRIPTION("Receta médica"), // Recetas médicas subidas por pacientes
    LAB_RESULT("Resultado de laboratorio"),
    XRAY("Radiografía"),
    ULTRASOUND("Ecografía"),
    MRI("Resonancia magnética"),
    CT_SCAN("Tomografía computarizada"),
    AUDIO("Audio"),
    VIDEO("Video"),
    OTHER("Otro");
    
    private final String displayName;
    
    FileType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}