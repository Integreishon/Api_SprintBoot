package com.hospital.backend.payment.dto;

import com.hospital.backend.enums.PaymentMethodType;
import com.hospital.backend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para el resumen de pagos y reportes financieros
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {

    // Resumen financiero general
    private BigDecimal totalRevenue;
    private BigDecimal totalFees;
    private BigDecimal netRevenue;
    private Long totalTransactions;
    private BigDecimal averageTransactionAmount;
    
    // Resumen por método de pago
    private BigDecimal cashPayments;
    private BigDecimal cardPayments;
    private BigDecimal transferPayments;
    private BigDecimal insurancePayments;
    private BigDecimal onlinePayments;
    
    // Resumen por estado
    private Long completedPayments;
    private Long pendingPayments;
    private Long refundedPayments;
    private Long cancelledPayments;
    
    // Fechas del período
    private LocalDate startDate;
    private LocalDate endDate;
} 