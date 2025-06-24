// Entidad Doctor para gestión de personal médico
package com.hospital.backend.user.entity;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.DoctorType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad Doctor para gestión de personal médico
 * Incluye clasificación por tipo y capacidad de derivación
 */
@Data
@Entity
@Table(name = "doctors")
@EqualsAndHashCode(callSuper = true)
public class Doctor extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "cmp_number", nullable = false, unique = true)
    private String cmpNumber;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "second_last_name")
    private String secondLastName;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "consultation_room")
    private String consultationRoom;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "doctor_type", nullable = false)
    private DoctorType doctorType = DoctorType.SPECIALIST; // PRIMARY, SPECIALIST
    
    @Column(name = "is_external", nullable = false)
    private Boolean isExternal = false;
    
    @Column(name = "can_refer", nullable = false)
    private Boolean canRefer = false;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoctorSpecialty> specialties;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DoctorAvailability> availability;
}