package com.hospital.backend.payment.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.dto.request.CreatePaymentRequest;
import com.hospital.backend.payment.dto.response.PaymentResponse;
import com.hospital.backend.payment.dto.response.PaymentSummaryResponse;
import com.hospital.backend.payment.service.PaymentService;
import com.hospital.backend.payment.service.MercadoPagoService;
import com.hospital.backend.appointment.entity.Appointment;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

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
    private final MercadoPagoService mercadoPagoService;
    private final com.hospital.backend.appointment.repository.AppointmentRepository appointmentRepository;
    
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

    // =========================
    // Mercado Pago Integration - MEJORADO
    // =========================

    @Operation(summary = "Crear preferencia de pago en Mercado Pago")
    @PostMapping("/mercadopago/create-preference")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createMercadoPagoPreference(
            @Parameter(description = "ID de la cita para la cual crear el pago") @RequestParam Long appointmentId,
            @Parameter(description = "Monto del pago") @RequestParam(required = false) BigDecimal amount) {
        
        log.info("🚀 POST /api/payments/mercadopago/create-preference - Iniciando creación de preferencia");
        log.info("📋 Parámetros recibidos: appointmentId={}, amount={}", appointmentId, amount);
        
        try {
            // Validación de parámetros
            if (appointmentId == null) {
                log.error("❌ appointmentId es null");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("El ID de la cita es requerido"));
            }
            
            // Si no se proporciona el monto, intentar obtenerlo de la cita
            if (amount == null) {
                log.info("🔍 Monto no proporcionado, obteniendo de la cita...");
                try {
                    Appointment appointment = appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + appointmentId));
                    
                    log.info("✅ Cita encontrada: {}", appointment.getId());
                    
                    // Intentar múltiples fuentes para el precio
                    amount = appointment.getPrice();
                    log.info("💰 Precio de la cita: {}", amount);
                    
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        if (appointment.getSpecialty() != null) {
                            amount = appointment.getSpecialty().getConsultationPrice();
                            log.info("💰 Precio de la especialidad: {}", amount);
                        }
                    }
                    
                    // Precio por defecto para pruebas si no se encuentra ningún precio válido
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        amount = new BigDecimal("50.00");
                        log.warn("⚠️ Usando precio por defecto para pruebas: {}", amount);
                    }
                    
                } catch (Exception e) {
                    log.error("❌ Error al obtener la cita: {}", e.getMessage());
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Error al obtener información de la cita: " + e.getMessage()));
                }
            }
            
            log.info("💰 Monto final para Mercado Pago: {}", amount);
            
            // Validación final del monto
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("❌ Monto inválido después de todos los intentos: {}", amount);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No se pudo determinar un monto válido para el pago"));
            }
            
            // USAR VERSIÓN SIMPLIFICADA PARA EVITAR PROBLEMAS CON autoReturn
            log.info("🔧 Usando versión simplificada de creación de preferencia...");
            String preferenceId = mercadoPagoService.createSimplePaymentPreference(appointmentId, amount);
            
            log.info("✅ Preferencia creada exitosamente: {}", preferenceId);
            return ResponseEntity.ok(ApiResponse.success("Preferencia de Mercado Pago creada exitosamente", preferenceId));
            
        } catch (MPApiException e) {
            log.error("❌ Error de API de Mercado Pago:", e);
            log.error("📝 Detalles: status={}, message={}, response={}", 
                    e.getStatusCode(), e.getMessage(), e.getApiResponse());
            
            String userMessage = "Error en la API de Mercado Pago";
            if (e.getStatusCode() == 400) {
                userMessage = "Datos inválidos para el pago. Verifique la información.";
            } else if (e.getStatusCode() == 401) {
                userMessage = "Credenciales de Mercado Pago inválidas.";
            } else if (e.getStatusCode() >= 500) {
                userMessage = "Error temporal en Mercado Pago. Intente nuevamente.";
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(userMessage + " (Código: " + e.getStatusCode() + ")"));
                    
        } catch (MPException e) {
            log.error("❌ Error general de Mercado Pago:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error de conexión con Mercado Pago: " + e.getMessage()));
                    
        } catch (Exception e) {
            log.error("❌ Error inesperado:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error inesperado: " + e.getMessage()));
        }
    }

    @Operation(summary = "Webhook para notificaciones de Mercado Pago")
    @PostMapping("/mercadopago/webhook")
    public ResponseEntity<Void> handleMercadoPagoWebhook(@RequestBody Map<String, Object> notification) {
        log.info("📢 POST /api/payments/mercadopago/webhook - Notificación recibida");
        log.info("📋 Contenido: {}", notification);
        
        try {
            mercadoPagoService.processWebhookNotification(notification);
            log.info("✅ Webhook procesado exitosamente");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ Error procesando webhook:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Validar configuración de Mercado Pago")
    @GetMapping("/mercadopago/validate-config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> validateMercadoPagoConfig() {
        log.info("🔍 GET /api/payments/mercadopago/validate-config - Validando configuración");
        
        try {
            String validationReport = mercadoPagoService.validateMercadoPagoConfig();
            log.info("✅ Validación completada");
            
            // Determinar si la configuración es válida basándose en el contenido del reporte
            boolean isValid = validationReport.contains("✅ CONFIGURACIÓN VÁLIDA");
            
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body(validationReport);
        } catch (Exception e) {
            log.error("❌ Error en validación:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body("❌ ERROR EN VALIDACIÓN: " + e.getMessage());
        }
    }

    @Operation(summary = "Diagnóstico completo de Mercado Pago para una cita")
    @GetMapping("/mercadopago/diagnose/{appointmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> diagnoseMercadoPago(@PathVariable Long appointmentId) {
        log.info("🩺 GET /api/payments/mercadopago/diagnose/{} - Ejecutando diagnóstico completo", appointmentId);
        
        try {
            String diagnosis = mercadoPagoService.diagnoseMercadoPagoPreference(appointmentId);
            log.info("✅ Diagnóstico completado para cita {}", appointmentId);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body(diagnosis);
        } catch (Exception e) {
            log.error("❌ Error en diagnóstico:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body("❌ ERROR EN DIAGNÓSTICO: " + e.getMessage());
        }
    }

    @Operation(summary = "Prueba rápida de creación de preferencia (solo para debug)")
    @PostMapping("/mercadopago/test-preference")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> testMercadoPagoPreference() {
        log.info("🧪 POST /api/payments/mercadopago/test-preference - Prueba de preferencia");
        
        try {
            // Crear una preferencia de prueba muy simple
            String preferenceId = mercadoPagoService.createTestPreference();
            
            return ResponseEntity.ok(ApiResponse.success("Preferencia de prueba creada", preferenceId));
            
        } catch (Exception e) {
            log.error("❌ Error en prueba:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en prueba: " + e.getMessage()));
        }
    }

    @Operation(summary = "Prueba avanzada con datos de cita real")
    @PostMapping("/mercadopago/test-with-appointment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> testWithRealAppointment() {
        log.info("🧪 POST /api/payments/mercadopago/test-with-appointment - Prueba con cita real");
        
        try {
            // Buscar cualquier cita para usar como referencia
            Appointment testAppointment = appointmentRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElse(null);
            
            if (testAppointment == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No hay citas disponibles para la prueba"));
            }
            
            log.info("🧪 Usando cita de prueba: {}", testAppointment.getId());
            
            // Usar un precio fijo para evitar problemas
            BigDecimal testAmount = new BigDecimal("10.00");
            
            String preferenceId = mercadoPagoService.createSimplePaymentPreference(testAppointment.getId(), testAmount);
            
            return ResponseEntity.ok(ApiResponse.success("Preferencia con cita real creada", preferenceId));
            
        } catch (Exception e) {
            log.error("❌ Error en prueba con cita real:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en prueba: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Crear preferencia con versión original (para comparar)")
    @PostMapping("/mercadopago/create-preference-original")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createOriginalPreference(
            @Parameter(description = "ID de la cita") @RequestParam Long appointmentId,
            @Parameter(description = "Monto del pago") @RequestParam(required = false) BigDecimal amount) {
        
        log.info("🧪 POST /api/payments/mercadopago/create-preference-original - Versión original");
        
        try {
            if (amount == null) {
                amount = new BigDecimal("50.00");
            }
            
            String preferenceId = mercadoPagoService.createPaymentPreference(appointmentId, amount);
            
            return ResponseEntity.ok(ApiResponse.success("Preferencia original creada", preferenceId));
            
        } catch (Exception e) {
            log.error("❌ Error en versión original:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en versión original: " + e.getMessage()));
        }
    }
}