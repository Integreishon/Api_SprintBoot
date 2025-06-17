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
    
    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;
    
    @Column(name = "payer_name", length = 150)
    private String payerName;
    
    @Column(name = "payer_email", length = 150)
    private String payerEmail;
    
    // =========================
    // Métodos de negocio
    // =========================
    
    /**
     * Verificar si el pago está completado
     */
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Verificar si el pago puede ser reembolsado
     */
    public boolean canRefund() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Verificar si el pago está pendiente
     */
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }
    
    /**
     * Verificar si el pago falló
     */
    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }
    
    /**
     * Marcar el pago como completado
     */
    public void markAsPaid(String transactionReference) {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.transactionReference = transactionReference;
    }
    
    /**
     * Marcar el pago como fallido
     */
    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.paymentDate = LocalDateTime.now();
    }
    
    /**
     * Marcar el pago como reembolsado
     */
    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }
    
    /**
     * Calcular el monto total incluyendo fees
     */
    public void calculateTotalAmount() {
        if (amount != null) {
            BigDecimal fee = processingFee != null ? processingFee : BigDecimal.ZERO;
            this.totalAmount = amount.add(fee);
        }
    }
}
