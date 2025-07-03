package com.hospital.backend.payment.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.payment.service.MercadoPagoService;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para probar la integración con Mercado Pago
 * SOLO PARA DESARROLLO - ELIMINAR EN PRODUCCIÓN
 */
@RestController
@RequestMapping("/payments/mercadopago/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tests de Mercado Pago", description = "Endpoints para probar la integración con Mercado Pago")
public class MercadoPagoTestController {

    private final MercadoPagoService mercadoPagoService;

    @Operation(summary = "Probar configuración de Mercado Pago")
    @GetMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> testMercadoPagoConfig() {
        log.info("GET /api/payments/mercadopago/test/validate - Validando configuración de Mercado Pago");
        
        try {
            // Obtener el reporte de validación
            String validationReport = mercadoPagoService.validateMercadoPagoConfig();
            
            // Determinar si es válida basándose en el contenido del reporte
            boolean isValid = validationReport.contains("✅ CONFIGURACIÓN VÁLIDA");
            
            log.info("Resultado de validación: {}", isValid ? "VÁLIDA" : "INVÁLIDA");
            log.debug("Reporte completo: {}", validationReport);
            
            return ResponseEntity.ok(ApiResponse.success(
                    isValid ? "Configuración de Mercado Pago válida" : "Configuración de Mercado Pago inválida", 
                    isValid));
        } catch (Exception e) {
            log.error("Error durante la validación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al validar configuración: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener reporte detallado de configuración")
    @GetMapping("/validate-detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getDetailedValidationReport() {
        log.info("GET /api/payments/mercadopago/test/validate-detailed - Obteniendo reporte detallado");
        
        try {
            String validationReport = mercadoPagoService.validateMercadoPagoConfig();
            
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body(validationReport);
        } catch (Exception e) {
            log.error("Error al obtener reporte detallado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body("❌ ERROR AL OBTENER REPORTE: " + e.getMessage());
        }
    }
    
    @Operation(summary = "Crear una preferencia de prueba básica")
    @GetMapping("/create-basic-preference")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createBasicPreference() {
        log.info("GET /api/payments/mercadopago/test/create-basic-preference - Creando preferencia básica");
        
        try {
            // Crear preferencia de prueba simple
            PreferenceClient client = new PreferenceClient();
            
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(
                    PreferenceItemRequest.builder()
                            .title("Producto de prueba")
                            .description("Descripción del producto de prueba")
                            .quantity(1)
                            .unitPrice(new BigDecimal("100.00"))
                            .currencyId("PEN")
                            .build());
            
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .externalReference("TEST_REFERENCE")
                    .build();
            
            Preference preference = client.create(request);
            String preferenceId = preference.getId();
            
            log.info("Preferencia de prueba creada con ID: {}", preferenceId);
            
            return ResponseEntity.ok(ApiResponse.success("Preferencia de prueba creada exitosamente", preferenceId));
        } catch (MPException | MPApiException e) {
            log.error("Error al crear preferencia de prueba: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al crear preferencia de prueba: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error inesperado: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener información detallada de MercadoPago")
    @GetMapping("/sdk-info")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getMercadoPagoSdkInfo() {
        log.info("GET /api/payments/mercadopago/test/sdk-info - Obteniendo información de SDK");
        
        StringBuilder info = new StringBuilder();
        try {
            info.append("=== INFORMACIÓN DE MERCADO PAGO SDK ===\n\n");
            
            // Access Token
            String accessToken = com.mercadopago.MercadoPagoConfig.getAccessToken();
            info.append("Access Token: ").append(accessToken != null ? 
                    (accessToken.substring(0, Math.min(10, accessToken.length())) + "...") : "null").append("\n");
            
            // SDK Version
            info.append("SDK Class: ").append(com.mercadopago.MercadoPagoConfig.class.getName()).append("\n");
            
            // Class información
            info.append("\nClases disponibles:\n");
            info.append("- PreferenceClient: ").append(PreferenceClient.class.getName()).append("\n");
            info.append("- PreferenceRequest: ").append(PreferenceRequest.class.getName()).append("\n");
            info.append("- Preference: ").append(Preference.class.getName()).append("\n");
            
            return ResponseEntity.ok(ApiResponse.success("Información de SDK obtenida", info.toString()));
        } catch (Exception e) {
            log.error("Error al obtener información de SDK: {}", e.getMessage(), e);
            info.append("\nERROR: ").append(e.getMessage()).append("\n");
            info.append("Tipo: ").append(e.getClass().getName()).append("\n");
            
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error al obtener información: " + info.toString()));
        }
    }
}