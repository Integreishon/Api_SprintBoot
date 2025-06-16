package com.hospital.backend.user.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.user.dto.request.CreateDoctorRequest;
import com.hospital.backend.user.dto.request.DoctorAvailabilityRequest;
import com.hospital.backend.user.dto.response.DoctorAvailabilityResponse;
import com.hospital.backend.user.dto.response.DoctorResponse;
import com.hospital.backend.user.service.DoctorAvailabilityService;
import com.hospital.backend.user.service.DoctorService;
import com.hospital.backend.user.service.ProfileImageService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Tag(name = "👨‍⚕️ Doctores", description = "Gestión de doctores: registro, especialidades, horarios y disponibilidad.")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorAvailabilityService availabilityService;
    private final ProfileImageService profileImageService;
    
    @GetMapping
    @Operation(summary = "Listar todos los doctores", description = "Obtiene un listado paginado de doctores")
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponse>>> getAllDoctors(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<DoctorResponse> doctors = doctorService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Doctores recuperados exitosamente", doctors));
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
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponse>>> getDoctorsBySpecialty(
            @PathVariable Long specialtyId,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<DoctorResponse> doctors = doctorService.findBySpecialty(specialtyId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Doctores por especialidad recuperados", doctors));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar doctores", description = "Busca doctores por nombre o apellido")
    public ResponseEntity<ApiResponse<PageResponse<DoctorResponse>>> searchDoctors(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<DoctorResponse> doctors = doctorService.searchDoctors(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", doctors));
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
    
    // Endpoint opcional para subir imagen de perfil (puedes implementar en el futuro)
   
    @PostMapping("/{id}/profile-image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Subir imagen de perfil", description = "Sube una imagen de perfil para el doctor")
    public ResponseEntity<ApiResponse<DoctorResponse>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            String imageUrl = profileImageService.saveProfileImage(imageFile, id);
            // Aquí necesitarías actualizar el doctor con la nueva imagen
            // DoctorResponse updatedDoctor = doctorService.updateProfileImage(id, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("Imagen subida exitosamente", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al subir imagen: " + e.getMessage()));
        }
    }
   
}
