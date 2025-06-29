// DTO para crear nuevo paciente
package com.hospital.backend.user.dto.request;

import com.hospital.backend.enums.BloodType;
import com.hospital.backend.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear nuevo paciente
 * Adaptado a la nueva lógica de Urovital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres")
    private String password;
    
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres")
    private String lastName;
    
    @Size(max = 100, message = "El apellido materno no puede exceder 100 caracteres")
    private String secondLastName;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate birthDate;
    
    @NotNull(message = "El género es obligatorio")
    private Gender gender;
    
    private BloodType bloodType;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Teléfono inválido (7-15 dígitos, solo números y +)")
    private String phone;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Teléfono de emergencia inválido (7-15 dígitos)")
    private String emergencyContactPhone;
    
    private String address;
    private String allergies;
    private String medicalHistory;
}