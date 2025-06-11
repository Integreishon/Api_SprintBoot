package com.hospital.backend.user.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.user.dto.request.CreateDoctorRequest;
import com.hospital.backend.user.dto.request.DoctorAvailabilityRequest;
import com.hospital.backend.user.dto.response.DoctorAvailabilityResponse;
import com.hospital.backend.user.dto.response.DoctorResponse;
import com.hospital.backend.user.service.DoctorAvailabilityService;
import com.hospital.backend.user.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctores", description = "API para gestión de doctores")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorAvailabilityService availabilityService;
    
    @GetMapping
    @Operation(summary = "Listar todos los doctores", description = "Obtiene un listado paginado de doctores")
    public ResponseEntity<PageResponse<DoctorResponse>> getAllDoctors(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(doctorService.findAll(pageable));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener doctor por ID", description = "Recupera los datos de un doctor específico por su ID")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long id) {
        DoctorResponse doctor = doctorService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor encontrado", doctor));
    }
    
    @GetMapping("/specialty/{specialtyId}")
    @Operation(summary = "Listar doctores por especialidad", description = "Obtiene un listado paginado de doctores filtrados por especialidad")
    public ResponseEntity<PageResponse<DoctorResponse>> getDoctorsBySpecialty(
            @PathVariable Long specialtyId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(doctorService.findBySpecialty(specialtyId, pageable));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar doctores", description = "Busca doctores por nombre o apellido")
    public ResponseEntity<PageResponse<DoctorResponse>> searchDoctors(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(doctorService.searchDoctors(query, pageable));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear doctor", description = "Registra un nuevo doctor en el sistema")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody CreateDoctorRequest request) {
        DoctorResponse createdDoctor = doctorService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor creado exitosamente", createdDoctor));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar doctor", description = "Actualiza los datos de un doctor existente")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody CreateDoctorRequest request) {
        DoctorResponse updatedDoctor = doctorService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Doctor actualizado exitosamente", updatedDoctor));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar doctor", description = "Elimina un doctor del sistema")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(
            @PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor eliminado exitosamente", null));
    }
    
    // Endpoints para gestión de disponibilidad
    
    @GetMapping("/{id}/availability")
    @Operation(summary = "Obtener disponibilidad por doctor", description = "Recupera los horarios disponibles de un doctor por día de la semana")
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityResponse>>> getDoctorAvailability(
            @PathVariable Long id) {
        List<DoctorAvailabilityResponse> availability = availabilityService.findByDoctorId(id);
        return ResponseEntity.ok(ApiResponse.success("Disponibilidad recuperada", availability));
    }
    
    @PostMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configurar disponibilidad", description = "Configura los horarios disponibles de un doctor")
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityResponse>>> setDoctorAvailability(
            @PathVariable Long id,
            @Valid @RequestBody List<DoctorAvailabilityRequest> request) {
        List<DoctorAvailabilityResponse> savedAvailability = availabilityService.saveAvailabilities(id, request);
        return ResponseEntity.ok(ApiResponse.success("Disponibilidad guardada", savedAvailability));
    }
    
    @DeleteMapping("/availability/{availabilityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar slot de disponibilidad", description = "Elimina un horario específico de disponibilidad")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(
            @PathVariable Long availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.ok(ApiResponse.success("Horario eliminado", null));
    }
}
