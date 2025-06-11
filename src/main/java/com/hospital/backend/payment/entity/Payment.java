package com.hospital.backend.payment.entity;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.catalog.entity.PaymentMethod;
import com.hospital.backend.common.entity.BaseEntity;
import com.hospital.backend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un pago en el sistema hospitalario
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "processing_fee", precision = 10, scale = 2)
    private BigDecimal processingFee;
    
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;
    
    @Column(name = "payer_name", length = 150)
    private String payerName;
    
    @Column(name = "payer_email", length = 150)
    private String payerEmail;
    
    // Métodos de negocio
    
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    public boolean canRefund() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    public void markAsPaid(String transactionReference) {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.transactionReference = transactionReference;
    }
    
    public void markAsRefunded(String notes) {
        this.status = PaymentStatus.REFUNDED;
        this.notes = notes;
    }
} 