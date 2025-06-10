// Entidad Paciente para gestión de datos médicos
package com.hospital.backend.user.entity;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.catalog.entity.DocumentType;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "patients")
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;
    
    @Column(name = "document_number", nullable = false)
    private String documentNumber;
    
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
    
    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;
    
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"document_type_id", "document_number"}))
    public static class PatientConstraints {}
}