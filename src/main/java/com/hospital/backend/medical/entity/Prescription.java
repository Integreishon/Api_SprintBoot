package com.hospital.backend.medical.entity;

import com.hospital.backend.common.entity.AuditEntity;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad que representa una receta médica
 */
@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prescription extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;
    
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "medication_name", nullable = false)
    private String medicationName;
    
    @Column(name = "dosage", nullable = false)
    private String dosage;
    
    @Column(name = "frequency", nullable = false)
    private String frequency;
    
    @Column(name = "duration", nullable = false)
    private String duration;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "refills")
    private Integer refills;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "dispensed")
    private Boolean dispensed = false;
    
    @Column(name = "dispensed_date")
    private LocalDateTime dispensedDate;
    
    @Column(name = "dispensed_by")
    private String dispensedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 