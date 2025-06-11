// Enumeración de fuente de subida de archivos
package com.hospital.backend.enums;

/**
 * Fuentes de carga de archivos en el sistema
 */
public enum UploadSource {
    DOCTOR("Doctor"),
    PATIENT("Paciente"),
    SYSTEM("Sistema"),
    LABORATORY("Laboratorio"),
    ADMINISTRATIVE("Administrativo");
    
    private final String displayName;
    
    UploadSource(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}