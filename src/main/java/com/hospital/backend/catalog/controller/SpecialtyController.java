package com.hospital.backend.catalog.controller;

import com.hospital.backend.catalog.dto.request.SpecialtyRequest;
import com.hospital.backend.catalog.dto.response.SpecialtyResponse;
import com.hospital.backend.catalog.service.SpecialtyService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
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
 * Controlador REST para la gestión de especialidades médicas
 */
@RestController
@RequestMapping("/specialties")
@RequiredArgsConstructor
@Tag(name = "🏥 Especialidades", description = "Catálogo de especialidades médicas con precios y configuración.")
public class SpecialtyController {
    
    private final SpecialtyService specialtyService;
    
    @GetMapping
    @Operation(summary = "Listar todas las especialidades", 
               description = "Obtiene un listado paginado de especialidades médicas")
    public ResponseEntity<PageResponse<SpecialtyResponse>> getAllSpecialties(
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        
        if (active != null) {
            return ResponseEntity.ok(specialtyService.findByActiveStatus(active, pageable));
        }
        
        return ResponseEntity.ok(specialtyService.findAll(pageable));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Listar especialidades activas", 
               description = "Obtiene un listado de todas las especialidades activas (no paginado)")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getAllActiveSpecialties() {
        List<SpecialtyResponse> specialties = specialtyService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success("Especialidades activas recuperadas", specialties));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener especialidad por ID", 
               description = "Recupera una especialidad específica por su ID")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> getSpecialtyById(@PathVariable Long id) {
        SpecialtyResponse specialty = specialtyService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Especialidad encontrada", specialty));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar especialidades por nombre", 
               description = "Busca especialidades que coincidan parcialmente con el nombre proporcionado")
    public ResponseEntity<PageResponse<SpecialtyResponse>> searchSpecialties(
            @RequestParam String name,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(specialtyService.searchByName(name, pageable));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear especialidad", 
               description = "Registra una nueva especialidad médica en el sistema")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> createSpecialty(
            @Valid @RequestBody SpecialtyRequest request) {
        
        SpecialtyResponse createdSpecialty = specialtyService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Especialidad creada exitosamente", createdSpecialty));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar especialidad", 
               description = "Actualiza una especialidad médica existente")
    public ResponseEntity<ApiResponse<SpecialtyResponse>> updateSpecialty(
            @PathVariable Long id, 
            @Valid @RequestBody SpecialtyRequest request) {
        
        SpecialtyResponse updatedSpecialty = specialtyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Especialidad actualizada exitosamente", updatedSpecialty));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar especialidad", 
               description = "Elimina una especialidad del sistema (marcándola como inactiva)")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialty(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Especialidad eliminada exitosamente", null));
    }
}
