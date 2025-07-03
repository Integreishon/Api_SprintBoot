// Entidad Paciente para gestión de datos médicos
package com.hospital.backend.user.entity;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.BloodType;
import com.hospital.backend.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Entidad Paciente para gestión de datos médicos
 * Simplificado: Solo DNI como documento
 */
@Data
@Entity
@Table(name = "patients")
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "second_last_name")
    private String secondLastName;
    
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodType bloodType;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(name = "reniec_verified", nullable = false)
    private Boolean reniecVerified = false;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}