package com.hospital.backend.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resúmenes de pagos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {
    
    private BigDecimal totalRevenue;
    private BigDecimal totalFees;
    private BigDecimal netRevenue;
    private Long totalTransactions;
    private BigDecimal averageTransactionAmount;
    
    // Por tipo de pago
    private BigDecimal cashPayments;
    private BigDecimal cardPayments;
    private BigDecimal digitalPayments;
    
    // Por estado
    private Long completedPayments;
    private Long processingPayments;
    private Long refundedPayments;
    private Long failedPayments;
    
    // Fechas
    private LocalDate startDate;
    private LocalDate endDate;
} 