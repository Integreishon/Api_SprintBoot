package com.hospital.backend.payment.dto;

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
    private Long failedPayments; // Corregido de 'cancelledPayments' a 'failedPayments'
    
    // Fechas del período
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Métricas adicionales
    private BigDecimal successRate; // Porcentaje de pagos exitosos
    private BigDecimal averageDailyRevenue; // Promedio de ingresos diarios
    
    /**
     * Calcular la tasa de éxito de pagos
     */
    public BigDecimal calculateSuccessRate() {
        if (totalTransactions == null || totalTransactions == 0) {
            return BigDecimal.ZERO;
        }
        
        long completedCount = completedPayments != null ? completedPayments : 0;
        return BigDecimal.valueOf(completedCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTransactions), 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Calcular el promedio de ingresos diarios
     */
    public BigDecimal calculateAverageDailyRevenue() {
        if (startDate == null || endDate == null || totalRevenue == null) {
            return BigDecimal.ZERO;
        }
        
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysDiff <= 0) {
            return BigDecimal.ZERO;
        }
        
        return totalRevenue.divide(BigDecimal.valueOf(daysDiff), 2, java.math.RoundingMode.HALF_UP);
    }
}
