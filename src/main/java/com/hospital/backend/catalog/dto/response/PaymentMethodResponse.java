package com.hospital.backend.catalog.dto.response;

import com.hospital.backend.enums.PaymentMethodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
    private Long id;
    private String name;
    private PaymentMethodType type;
    private String typeName;
    private BigDecimal processingFee;
    private Boolean isActive;
    private String integrationCode;
} 