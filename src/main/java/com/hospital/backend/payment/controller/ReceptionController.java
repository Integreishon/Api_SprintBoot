package com.hospital.backend.payment.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.payment.dto.request.RejectPaymentRequest;
import com.hospital.backend.payment.dto.response.PendingValidationResponse;
import com.hospital.backend.payment.service.ReceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para el portal de validación de citas virtuales
 * Expone endpoints para que la recepcionista valide pagos y marque la llegada de pacientes
 */
@RestController
@RequestMapping("/api/reception")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEPTIONIST')")
@Slf4j
@Tag(name = "🏥 Portal de Recepción", description = "Gestión de citas virtuales por parte del personal de recepción: validación de pagos y registro de llegada.")
public class ReceptionController {

    private final ReceptionService receptionService;
    
    /**
     * Obtiene la lista de citas pendientes de validación
     * @return Lista de citas pendientes
     */
    @Operation(summary = "Obtener citas pendientes de validación")
    @GetMapping("/pending-validation")
    public ResponseEntity<ApiResponse<List<PendingValidationResponse>>> getPendingValidations() {
        log.info("GET /api/reception/pending-validation - Obteniendo citas pendientes de validación");
        List<PendingValidationResponse> pendingValidations = receptionService.getAppointmentsPendingValidation();
        return ResponseEntity.ok(ApiResponse.success("Citas pendientes de validación obtenidas", pendingValidations));
    }
    
    /**
     * Valida un pago pendiente
     * @param paymentId ID del pago a validar
     * @param userDetails Detalles del usuario (recepcionista) que realiza la validación
     * @return Resultado de la validación
     */
    @Operation(summary = "Validar pago de cita virtual")
    @PostMapping("/payments/{paymentId}/validate")
    public ResponseEntity<ApiResponse<Boolean>> validatePayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("POST /api/reception/payments/{}/validate - Validando pago", paymentId);
        boolean result = receptionService.validatePayment(paymentId, userDetails);
        return ResponseEntity.ok(ApiResponse.success("Pago validado exitosamente", result));
    }
    
    /**
     * Rechaza un pago pendiente
     * @param paymentId ID del pago a rechazar
     * @param request Contiene el motivo del rechazo
     * @return Resultado del rechazo
     */
    @Operation(summary = "Rechazar pago de cita virtual")
    @PostMapping("/payments/{paymentId}/reject")
    public ResponseEntity<ApiResponse<Boolean>> rejectPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody RejectPaymentRequest request) {
        
        log.info("POST /api/reception/payments/{}/reject - Rechazando pago", paymentId);
        boolean result = receptionService.rejectPayment(paymentId, request.getReason());
        return ResponseEntity.ok(ApiResponse.success("Pago rechazado. El paciente será notificado.", result));
    }
    
    /**
     * Marca un paciente como llegado al centro médico
     * @param appointmentId ID de la cita
     * @return Resultado de la operación
     */
    @Operation(summary = "Marcar paciente como llegado")
    @PostMapping("/appointments/{appointmentId}/arrive")
    public ResponseEntity<ApiResponse<Boolean>> markPatientAsArrived(@PathVariable Long appointmentId) {
        log.info("POST /api/reception/appointments/{}/arrive - Marcando paciente como llegado", appointmentId);
        boolean result = receptionService.markPatientAsArrived(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Paciente marcado como llegado", result));
    }
} 