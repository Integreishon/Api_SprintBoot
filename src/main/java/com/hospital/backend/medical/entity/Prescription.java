package com.hospital.backend.medical.entity;

import com.hospital.backend.common.entity.AuditEntity;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Receta médica vinculada a historial clínico
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
    private LocalDate issueDate;

    @Column(name = "medication_name", nullable = false, length = 100)
    private String medicationName;

    @Column(name = "dosage", nullable = false, length = 50)
    private String dosage;

    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;

    @Column(name = "duration", nullable = false, length = 30)
    private String duration;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
} 