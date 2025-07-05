// DTO de respuesta para datos de paciente
package com.hospital.backend.user.dto.response;

import com.hospital.backend.enums.BloodType;
import com.hospital.backend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para datos de paciente
 * Adaptado a la nueva lógica de Urovital
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    
    private Long id;
    private Long userId;
    private String email;
    private String dni;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String fullName;
    private LocalDate birthDate;
    private Integer age;
    private Gender gender;
    private BloodType bloodType;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String address;
    private String allergies;
    private String medicalHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}