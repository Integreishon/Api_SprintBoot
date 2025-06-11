package com.hospital.backend.payment.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.CreatePaymentRequest;
import com.hospital.backend.payment.dto.PaymentResponse;
import com.hospital.backend.payment.dto.PaymentSummaryResponse;
import com.hospital.backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador para la gestión de pagos
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    
    /**
     * Crea un nuevo pago
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pago creado exitosamente", payment));
    }
    
    /**
     * Confirma un pago
     */
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            @RequestParam String transactionReference) {
        PaymentResponse payment = paymentService.confirmPayment(id, transactionReference);
        return ResponseEntity.ok(ApiResponse.success("Pago confirmado exitosamente", payment));
    }
    
    /**
     * Reembolsa un pago
     */
    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        PaymentResponse payment = paymentService.refundPayment(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Pago reembolsado exitosamente", payment));
    }
    
    /**
     * Obtiene un pago por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long id) {
        PaymentResponse payment = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.success("Pago obtenido exitosamente", payment));
    }
    
    /**
     * Obtiene un pago por ID de cita
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByAppointment(
            @PathVariable Long appointmentId) {
        PaymentResponse payment = paymentService.getPaymentByAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Pago obtenido exitosamente", payment));
    }
    
    /**
     * Lista todos los pagos paginados
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getAllPayments(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        PageResponse<PaymentResponse> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pagos obtenidos exitosamente", payments));
    }
    
    /**
     * Lista pagos por estado
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        PageResponse<PaymentResponse> payments = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Pagos obtenidos exitosamente", payments));
    }
    
    /**
     * Genera un resumen financiero de pagos por período
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> generatePaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        PaymentSummaryResponse summary = paymentService.generatePaymentSummary(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Resumen financiero generado exitosamente", summary));
    }
} 