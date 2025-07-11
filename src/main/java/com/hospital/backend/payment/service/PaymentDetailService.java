package com.hospital.backend.payment.service;

import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.payment.dto.response.PaymentDetailResponse;
import com.hospital.backend.payment.entity.PaymentDetail;
import com.hospital.backend.payment.repository.PaymentDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de detalles de pagos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentDetailService {
    
    private final PaymentDetailRepository paymentDetailRepository;
    
    /**
     * Obtiene los detalles de un pago por ID de pago
     */
    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetailByPaymentId(Long paymentId) {
        PaymentDetail paymentDetail = paymentDetailRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentDetail", "paymentId", paymentId));
        
        return mapToPaymentDetailResponse(paymentDetail);
    }
    
    /**
     * Verifica si existen detalles para un pago
     */
    @Transactional(readOnly = true)
    public boolean hasPaymentDetail(Long paymentId) {
        return paymentDetailRepository.existsByPaymentId(paymentId);
    }
    
    /**
     * Mapea una entidad PaymentDetail a un DTO PaymentDetailResponse
     */
    private PaymentDetailResponse mapToPaymentDetailResponse(PaymentDetail paymentDetail) {
        return PaymentDetailResponse.builder()
                .id(paymentDetail.getId())
                .paymentId(paymentDetail.getPayment().getId())
                .processorName(paymentDetail.getProcessorName())
                .processorTransactionId(paymentDetail.getProcessorTransactionId())
                .paymentMethodDetail(paymentDetail.getPaymentMethodDetail())
                .cardLastDigits(paymentDetail.getCardLastDigits())
                .cardType(paymentDetail.getCardType())
                .issuerBank(paymentDetail.getIssuerBank())
                .installments(paymentDetail.getInstallments())
                .currency(paymentDetail.getCurrency())
                .transactionStatus(paymentDetail.getTransactionStatus())
                .responseCode(paymentDetail.getResponseCode())
                .responseMessage(paymentDetail.getResponseMessage())
                .build();
    }
} 