// DTO de respuesta para datos de doctor
package com.hospital.backend.user.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DoctorResponse {
    
    private Long id;
    private Long userId;
    private String email;
    private String cmpNumber;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String fullName;
    private String phone;
    private String consultationRoom;
    private Boolean isActive;
    private LocalDate hireDate;
    private String biography;
    private String profileImage;
    private List<DoctorSpecialtyDto> specialties;
    private Long primarySpecialtyId;
    private String primarySpecialtyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class DoctorSpecialtyDto {
        private Long id;
        private Long specialtyId;
        private String specialtyName;
        private Boolean isPrimary;
        private LocalDate certificationDate;
    }
}