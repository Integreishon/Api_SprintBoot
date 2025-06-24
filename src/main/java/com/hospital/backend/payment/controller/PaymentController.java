package com.hospital.backend.payment.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.request.CreatePaymentRequest;
import com.hospital.backend.payment.dto.response.PaymentResponse;
import com.hospital.backend.payment.dto.response.PaymentSummaryResponse;
import com.hospital.backend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para la gestión de pagos
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "💰 Pagos y Facturación", description = "Gestión completa de pagos: procesamiento, confirmación, reportes financieros y métodos de pago.")
public class PaymentController {

    private final PaymentService paymentService;
    
    // =========================
    // CRUD Operations
    // =========================
    
    @Operation(summary = "Crear nuevo pago")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        
        log.info("POST /api/payments - Creando nuevo pago para cita: {}", request.getAppointmentId());
        PaymentResponse response = paymentService.createPayment(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pago creado exitosamente", response));
    }
    
    @Operation(summary = "Obtener pago por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        
        log.info("GET /api/payments/{} - Obteniendo pago por ID", id);
        PaymentResponse response = paymentService.getPayment(id);
        
        return ResponseEntity.ok(ApiResponse.success("Pago encontrado", response));
    }
    
    @Operation(summary = "Obtener pago por ID de cita")
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByAppointment(
            @Parameter(description = "ID de la cita") @PathVariable Long appointmentId) {
        
        log.info("GET /api/payments/appointment/{} - Obteniendo pago por cita", appointmentId);
        PaymentResponse response = paymentService.getPaymentByAppointment(appointmentId);
        
        return ResponseEntity.ok(ApiResponse.success("Pago de la cita encontrado", response));
    }
    
    @Operation(summary = "Listar todos los pagos")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getAllPayments(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        
        log.info("GET /api/payments - Listando pagos (página: {}, tamaño: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PaymentResponse> response = paymentService.getAllPayments(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Lista de pagos obtenida", response));
    }
    
    // =========================
    // Cambios de estado
    // =========================
    
    @Operation(summary = "Confirmar un pago pendiente")
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @Parameter(description = "ID del pago") @PathVariable Long id,
            @Parameter(description = "Referencia de transacción") @RequestParam String transactionReference) {
        
        log.info("PUT /api/payments/{}/confirm - Confirmando pago con referencia: {}", id, transactionReference);
        PaymentResponse response = paymentService.confirmPayment(id, transactionReference);
        
        return ResponseEntity.ok(ApiResponse.success("Pago confirmado exitosamente", response));
    }
    
    @Operation(summary = "Marcar pago como fallido")
    @PutMapping("/{id}/fail")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PaymentResponse>> markPaymentAsFailed(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        
        log.info("PUT /api/payments/{}/fail - Marcando pago como fallido", id);
        PaymentResponse response = paymentService.markPaymentAsFailed(id);
        
        return ResponseEntity.ok(ApiResponse.success("Pago marcado como fallido", response));
    }
    
    @Operation(summary = "Reembolsar un pago")
    @PutMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        
        log.info("PUT /api/payments/{}/refund - Procesando reembolso", id);
        PaymentResponse response = paymentService.refundPayment(id);
        
        return ResponseEntity.ok(ApiResponse.success("Pago reembolsado exitosamente", response));
    }
    
    // =========================
    // Consultas específicas
    // =========================
    
    @Operation(summary = "Obtener pagos por estado")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentsByStatus(
            @Parameter(description = "Estado del pago") @PathVariable PaymentStatus status,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        
        log.info("GET /api/payments/status/{} - Obteniendo pagos por estado", status);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PaymentResponse> response = paymentService.getPaymentsByStatus(status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pagos por estado obtenidos", response));
    }
    
    @Operation(summary = "Obtener pagos por rango de fechas")
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPaymentsByDateRange(
            @Parameter(description = "Fecha inicio (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        
        log.info("GET /api/payments/date-range - Obteniendo pagos desde {} hasta {}", startDate, endDate);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PaymentResponse> response = paymentService.getPaymentsByDateRange(startDate, endDate, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Pagos por rango de fechas obtenidos", response));
    }
    
    // =========================
    // Reportes y estadísticas
    // =========================
    
    @Operation(summary = "Generar resumen financiero")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> generatePaymentSummary(
            @Parameter(description = "Fecha inicio (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha fin (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/payments/summary - Generando resumen financiero desde {} hasta {}", startDate, endDate);
        PaymentSummaryResponse response = paymentService.generatePaymentSummary(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success("Resumen financiero generado", response));
    }
}
