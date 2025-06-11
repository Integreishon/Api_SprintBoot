package com.hospital.backend.admin.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.hospital.backend.admin.dto.SettingRequest;
import com.hospital.backend.admin.dto.SettingResponse;
import com.hospital.backend.admin.service.HospitalSettingService;
import com.hospital.backend.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador para gestionar las configuraciones del hospital
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class HospitalSettingController {

    private final HospitalSettingService settingService;
    
    /**
     * Obtiene todas las configuraciones del hospital
     * @return Lista de configuraciones
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SettingResponse>> getAllSettings() {
        log.info("GET /api/settings - Obteniendo todas las configuraciones");
        return ResponseEntity.ok(settingService.getAllSettings());
    }
    
    /**
     * Obtiene todas las configuraciones públicas
     * @return Lista de configuraciones públicas
     */
    @GetMapping("/public")
    public ResponseEntity<List<SettingResponse>> getPublicSettings() {
        log.info("GET /api/settings/public - Obteniendo configuraciones públicas");
        return ResponseEntity.ok(settingService.getPublicSettings());
    }
    
    /**
     * Obtiene configuraciones por categoría
     * @param category Categoría a buscar
     * @return Lista de configuraciones
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SettingResponse>> getSettingsByCategory(@PathVariable String category) {
        log.info("GET /api/settings/category/{} - Obteniendo configuraciones por categoría", category);
        return ResponseEntity.ok(settingService.getSettingsByCategory(category));
    }
    
    /**
     * Obtiene una configuración por su clave
     * @param key Clave de la configuración
     * @return Configuración encontrada
     */
    @GetMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPublicSetting(#key)")
    public ResponseEntity<SettingResponse> getSettingByKey(@PathVariable String key) {
        log.info("GET /api/settings/{} - Obteniendo configuración por clave", key);
        return ResponseEntity.ok(settingService.getSettingByKey(key));
    }
    
    /**
     * Crea una nueva configuración
     * @param request Datos de la configuración
     * @return Configuración creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingResponse> createSetting(@Valid @RequestBody SettingRequest request) {
        log.info("POST /api/settings - Creando nueva configuración: {}", request.getKey());
        return new ResponseEntity<>(settingService.createSetting(request), HttpStatus.CREATED);
    }
    
    /**
     * Actualiza una configuración existente
     * @param key Clave de la configuración
     * @param request Nuevos datos
     * @return Configuración actualizada
     */
    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SettingResponse> updateSetting(
            @PathVariable String key,
            @Valid @RequestBody SettingRequest request) {
        log.info("PUT /api/settings/{} - Actualizando configuración", key);
        return ResponseEntity.ok(settingService.updateSetting(key, request));
    }
    
    /**
     * Elimina una configuración
     * @param key Clave de la configuración
     * @return Respuesta de éxito
     */
    @DeleteMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSetting(@PathVariable String key) {
        log.info("DELETE /api/settings/{} - Eliminando configuración", key);
        settingService.deleteSetting(key);
        return ResponseEntity.ok(ApiResponse.success("Configuración eliminada correctamente"));
    }
    
    /**
     * Obtiene el valor de una configuración
     * @param key Clave de la configuración
     * @return Valor de la configuración
     */
    @GetMapping("/value/{key}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPublicSetting(#key)")
    public ResponseEntity<String> getSettingValue(@PathVariable String key) {
        log.info("GET /api/settings/value/{} - Obteniendo valor de configuración", key);
        return ResponseEntity.ok(settingService.getSettingValue(key));
    }
} 