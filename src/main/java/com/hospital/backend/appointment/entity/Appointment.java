package com.hospital.backend.appointment.entity;

import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una cita médica presencial en el sistema
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    // Primero se elige la especialidad...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;
    
    // ...y luego los doctores de esa especialidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;
    
    @Column(name = "follow_up_appointment_id")
    private Long followUpAppointmentId;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    // =========================
    // Métodos de negocio
    // =========================
    
    /**
     * ¿Se puede cancelar?
     * Solo si está programada o confirmada.
     */
    public boolean canCancel() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * ¿Se puede marcar como completada?
     * Solo si ya fue confirmada.
     */
    public boolean canComplete() {
        return this.status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * ¿Se puede reagendar?
     * Solo si está programada o confirmada.
     */
    public boolean canReschedule() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * ¿La cita está pendiente?
     * Programada o confirmada.
     */
    public boolean isPending() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
    
    /**
     * Calcular hora de fin de la cita
     */
    public LocalTime getEndTime() {
        return this.startTime.plusMinutes(30); // Duración fija de 30 minutos
    }
}
