package com.hospital.backend.payment.entity;

import com.hospital.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad para almacenar detalles adicionales de pagos
 * Esta tabla complementa a la tabla principal de pagos
 * con información específica de la transacción
 */
@Entity
@Table(name = "payment_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;
    
    // Información del procesador de pago
    @Column(name = "processor_name", length = 50)
    private String processorName;
    
    // ID de la transacción en el sistema del procesador de pago
    @Column(name = "processor_transaction_id", length = 100)
    private String processorTransactionId;
    
    // Método de pago específico (tarjeta, transferencia, etc.)
    @Column(name = "payment_method_detail", length = 50)
    private String paymentMethodDetail;
    
    // Últimos 4 dígitos de la tarjeta (si aplica)
    @Column(name = "card_last_digits", length = 4)
    private String cardLastDigits;
    
    // Tipo de tarjeta (si aplica): VISA, MASTERCARD, etc.
    @Column(name = "card_type", length = 20)
    private String cardType;
    
    // Banco emisor de la tarjeta (si aplica)
    @Column(name = "issuer_bank", length = 50)
    private String issuerBank;
    
    // Número de cuotas (si aplica)
    @Column(name = "installments")
    private Integer installments;
    
    // Moneda utilizada
    @Column(name = "currency", length = 3)
    private String currency;
    
    // Datos adicionales en formato JSON
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;
    
    // Estado detallado de la transacción
    @Column(name = "transaction_status", length = 50)
    private String transactionStatus;
    
    // Código de respuesta del procesador
    @Column(name = "response_code", length = 20)
    private String responseCode;
    
    // Mensaje de respuesta del procesador
    @Column(name = "response_message", length = 255)
    private String responseMessage;
} 