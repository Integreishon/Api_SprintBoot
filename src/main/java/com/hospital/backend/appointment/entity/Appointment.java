package com.hospital.backend.appointment.entity;

import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.enums.TimeBlock;
import com.hospital.backend.user.entity.Doctor;
import com.hospital.backend.user.entity.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa una cita médica en el sistema
 * IMPORTANTE: Las citas pueden existir con diferentes estados de pago
 * Todas las citas en este sistema son virtuales, ya que las presenciales se manejan en el sistema de Urovital
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "time_block", nullable = false)
    private TimeBlock timeBlock; // MORNING, AFTERNOON
    
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PROCESSING; // Cambiado de COMPLETED a PROCESSING
    
    // =========================
    // Métodos de negocio
    // =========================
    
    /**
     * ¿Se puede cancelar?
     * Solo si está pendiente de validación, programada o en consulta.
     */
    public boolean canCancel() {
        return this.status == AppointmentStatus.PENDING_VALIDATION ||
               this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.IN_CONSULTATION;
    }
    
    /**
     * ¿Se puede marcar como completada?
     * Solo si está en consulta.
     */
    public boolean canComplete() {
        return this.status == AppointmentStatus.IN_CONSULTATION;
    }
    
    /**
     * ¿La cita está pendiente?
     * Pendiente de validación, programada o en consulta.
     */
    public boolean isPending() {
        return this.status == AppointmentStatus.PENDING_VALIDATION ||
               this.status == AppointmentStatus.SCHEDULED || 
               this.status == AppointmentStatus.IN_CONSULTATION;
    }
    
    /**
     * ¿El pago está completo?
     */
    public boolean isPaymentCompleted() {
        return this.paymentStatus == PaymentStatus.COMPLETED;
    }
    
    /**
     * ¿El pago está en proceso?
     */
    public boolean isPaymentProcessing() {
        return this.paymentStatus == PaymentStatus.PROCESSING;
    }
    
    /**
     * ¿El pago falló?
     */
    public boolean isPaymentFailed() {
        return this.paymentStatus == PaymentStatus.FAILED;
    }
    
    /**
     * ¿El pago fue reembolsado?
     */
    public boolean isPaymentRefunded() {
        return this.paymentStatus == PaymentStatus.REFUNDED;
    }
    
    /**
     * Obtener el precio de la consulta
     * Se calcula automáticamente desde specialty.consultation_price
     */
    public java.math.BigDecimal getPrice() {
        return this.specialty != null ? this.specialty.getFinalPrice() : null;
    }
}
