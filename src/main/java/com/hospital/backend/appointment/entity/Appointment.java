package com.hospital.backend.appointment.entity;

import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
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
 * Entidad que representa una cita médica en el sistema
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "in_person", nullable = false)
    private Boolean inPerson = true;

    @Column(name = "virtual_meeting_url")
    private String virtualMeetingUrl;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    @Column(name = "follow_up_appointment_id")
    private Long followUpAppointmentId;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    // Métodos de negocio
    
    public boolean canCancel() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
    
    public boolean canComplete() {
        return this.status == AppointmentStatus.CONFIRMED;
    }
    
    public boolean canReschedule() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
    
    public boolean isPending() {
        return this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.CONFIRMED;
    }
} 