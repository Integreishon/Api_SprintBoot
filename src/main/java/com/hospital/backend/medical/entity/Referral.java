package com.hospital.backend.medical.entity;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.ReferralStatus;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Entidad para derivaciones médicas internas
 */
@Entity
@Table(name = "referrals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Referral extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referring_doctor_id", nullable = false)
    private Doctor referringDoctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_specialty_id", nullable = false)
    private Specialty targetSpecialty;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_appointment_id", nullable = false)
    private Appointment originalAppointment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_appointment_id")
    private Appointment newAppointment;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReferralStatus status;
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    // =========================
    // Métodos de negocio
    // =========================
    
    /**
     * ¿La derivación está pendiente?
     */
    public boolean isPending() {
        return this.status == ReferralStatus.REQUESTED;
    }
    
    /**
     * ¿La derivación está programada?
     */
    public boolean isScheduled() {
        return this.status == ReferralStatus.SCHEDULED;
    }
    
    /**
     * ¿La derivación está completada?
     */
    public boolean isCompleted() {
        return this.status == ReferralStatus.COMPLETED;
    }
    
    /**
     * Marcar como programada
     */
    public void markAsScheduled(Appointment newAppointment) {
        this.status = ReferralStatus.SCHEDULED;
        this.newAppointment = newAppointment;
        this.scheduledAt = LocalDateTime.now();
    }
    
    /**
     * Marcar como completada
     */
    public void markAsCompleted() {
        this.status = ReferralStatus.COMPLETED;
    }
    
    /**
     * Marcar como cancelada
     */
    public void markAsCancelled() {
        this.status = ReferralStatus.CANCELLED;
    }
} 