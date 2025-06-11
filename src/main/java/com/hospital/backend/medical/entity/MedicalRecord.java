package com.hospital.backend.medical.entity;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.common.entity.AuditEntity;
import com.hospital.backend.enums.SeverityLevel;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un registro médico en el historial de un paciente
 */
@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord extends AuditEntity {

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
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;
    
    @Column(name = "chief_complaint", nullable = false, columnDefinition = "TEXT")
    private String chiefComplaint;
    
    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;
    
    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "followup_required")
    private Boolean followupRequired = false;
    
    @Column(name = "followup_date")
    private LocalDateTime followupDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private SeverityLevel severity;
    
    @Column(name = "height_cm")
    private Double heightCm;
    
    @Column(name = "weight_kg")
    private Double weightKg;
    
    @Column(name = "blood_pressure")
    private String bloodPressure;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;
    
    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;
    
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prescription> prescriptions = new ArrayList<>();
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalAttachment> attachments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 