// DTO para actualizar datos de paciente
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
 * DTO para actualizar datos de paciente
 * Adaptado a la nueva lógica de Urovital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequest {
    
    @Email(message = "El formato del correo electrónico es inválido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;
    
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
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    private String phone;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono de emergencia inválido")
    private String emergencyContactPhone;
    
    private String address;
    private String allergies;
    private String medicalHistory;
}