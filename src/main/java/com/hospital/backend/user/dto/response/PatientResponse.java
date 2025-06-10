// DTO de respuesta para datos de paciente
package com.hospital.backend.user.dto.response;

import com.hospital.backend.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponse {
    
    private Long id;
    private Long userId;
    private String email;
    private String documentType;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String fullName;
    private LocalDate birthDate;
    private Integer age;
    private Gender gender;
    private String phone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String address;
    private String allergies;
    private String chronicConditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}