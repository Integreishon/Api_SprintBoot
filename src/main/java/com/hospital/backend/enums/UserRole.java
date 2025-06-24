package com.hospital.backend.enums;

/**
 * Roles de usuario en el sistema
 */
public enum UserRole {
    PATIENT,    // Pacientes del centro
    DOCTOR,     // Dr. Mario (can_refer=true)
    SPECIALIST, // Doctores bajo demanda
    RECEPTIONIST, // Personal registro/pago
    ADMIN       // Administradores
}