package com.hospital.backend.payment.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePreferenceRequest {
    private String title;
    private BigDecimal price;
} 