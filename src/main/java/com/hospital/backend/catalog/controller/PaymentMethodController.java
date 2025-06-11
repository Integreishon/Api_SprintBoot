package com.hospital.backend.catalog.controller;

import com.hospital.backend.catalog.dto.request.PaymentMethodRequest;
import com.hospital.backend.catalog.dto.response.PaymentMethodResponse;
import com.hospital.backend.catalog.service.PaymentMethodService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.enums.PaymentMethodType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de métodos de pago
 */
@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Tag(name = "Métodos de Pago", description = "API para gestión de métodos de pago")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    
    @GetMapping
    @Operation(summary = "Listar todos los métodos de pago", 
               description = "Obtiene un listado paginado de métodos de pago")
    public ResponseEntity<PageResponse<PaymentMethodResponse>> getAllPaymentMethods(
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        
        if (active != null) {
            return ResponseEntity.ok(paymentMethodService.findByActiveStatus(active, pageable));
        }
        
        return ResponseEntity.ok(paymentMethodService.findAll(pageable));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Listar métodos de pago activos", 
               description = "Obtiene un listado de todos los métodos de pago activos (no paginado)")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getAllActivePaymentMethods() {
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success("Métodos de pago activos recuperados", paymentMethods));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener método de pago por ID", 
               description = "Recupera un método de pago específico por su ID")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethodById(@PathVariable Long id) {
        PaymentMethodResponse paymentMethod = paymentMethodService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Método de pago encontrado", paymentMethod));
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Listar métodos de pago por tipo", 
               description = "Obtiene los métodos de pago activos de un tipo específico")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getPaymentMethodsByType(
            @PathVariable PaymentMethodType type) {
        
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.findByType(type);
        return ResponseEntity.ok(ApiResponse.success("Métodos de pago recuperados", paymentMethods));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear método de pago", 
               description = "Registra un nuevo método de pago en el sistema")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> createPaymentMethod(
            @Valid @RequestBody PaymentMethodRequest request) {
        
        PaymentMethodResponse createdPaymentMethod = paymentMethodService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Método de pago creado exitosamente", createdPaymentMethod));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar método de pago", 
               description = "Actualiza un método de pago existente")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable Long id, 
            @Valid @RequestBody PaymentMethodRequest request) {
        
        PaymentMethodResponse updatedPaymentMethod = paymentMethodService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Método de pago actualizado exitosamente", updatedPaymentMethod));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar método de pago", 
               description = "Elimina un método de pago del sistema (marcándolo como inactivo)")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Método de pago eliminado exitosamente", null));
    }
}
