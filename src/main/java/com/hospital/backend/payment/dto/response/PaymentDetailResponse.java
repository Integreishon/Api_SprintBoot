package com.hospital.backend.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para devolver información detallada de un pago
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResponse {
    
    // ID del detalle de pago
    private Long id;
    
    // ID del pago relacionado
    private Long paymentId;
    
    // Información del procesador de pago
    private String processorName;
    
    // ID de la transacción en el sistema del procesador de pago
    private String processorTransactionId;
    
    // Método de pago específico (tarjeta, transferencia, etc.)
    private String paymentMethodDetail;
    
    // Últimos 4 dígitos de la tarjeta (si aplica)
    private String cardLastDigits;
    
    // Tipo de tarjeta (si aplica): VISA, MASTERCARD, etc.
    private String cardType;
    
    // Banco emisor de la tarjeta (si aplica)
    private String issuerBank;
    
    // Número de cuotas (si aplica)
    private Integer installments;
    
    // Moneda utilizada
    private String currency;
    
    // Estado detallado de la transacción
    private String transactionStatus;
    
    // Código de respuesta del procesador
    private String responseCode;
    
    // Mensaje de respuesta del procesador
    private String responseMessage;
} 