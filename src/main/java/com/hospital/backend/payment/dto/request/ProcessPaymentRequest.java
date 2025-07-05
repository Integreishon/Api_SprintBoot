package com.hospital.backend.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProcessPaymentRequest {
    private String token;
    
    @JsonProperty("issuer_id")
    private String issuerId;
    
    @JsonProperty("payment_method_id")
    private String paymentMethodId;
    
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    
    private int installments;
    private Payer payer;
    private Long appointmentId;

    @Data
    public static class Payer {
        private String email;
        private Identification identification;
    }

    @Data
    public static class Identification {
        private String type;
        private String number;
    }
} 