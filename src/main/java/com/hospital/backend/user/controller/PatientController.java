package com.hospital.backend.user.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.user.dto.request.CreatePatientRequest;
import com.hospital.backend.user.dto.request.UpdatePatientRequest;
import com.hospital.backend.user.dto.response.PatientResponse;
import com.hospital.backend.user.service.PatientService;
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

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "🧑‍⚕️ Pacientes", description = "Gestión completa de pacientes: registro, consulta, actualización y búsqueda por documentos. Control de acceso y validaciones.")
public class PatientController {
    
    private final PatientService patientService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pacientes", description = "Obtiene un listado paginado de pacientes")
    public ResponseEntity<ApiResponse<PageResponse<PatientResponse>>> getAllPatients(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<PatientResponse> patients = patientService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pacientes recuperados exitosamente", patients));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @patientService.validateUserCanAccessPatient(#id, principal.id)")
    @Operation(summary = "Obtener paciente por ID", description = "Recupera los datos de un paciente específico por su ID")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long id) {
        PatientResponse patient = patientService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Paciente encontrado", patient));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar pacientes", description = "Busca pacientes por nombre o apellido")
    public ResponseEntity<ApiResponse<PageResponse<PatientResponse>>> searchPatients(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<PatientResponse> patients = patientService.searchPatients(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda completada", patients));
    }
    
    @GetMapping("/document")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar paciente por documento", description = "Busca un paciente por tipo y número de documento")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByDocument(
            @RequestParam String documentNumber,
            @RequestParam Long documentTypeId) {
        PatientResponse patient = patientService.findByDocumentNumberAndDocumentType(documentNumber, documentTypeId);
        return ResponseEntity.ok(ApiResponse.success("Paciente encontrado", patient));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear paciente", description = "Registra un nuevo paciente en el sistema")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        PatientResponse createdPatient = patientService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Paciente creado exitosamente", createdPatient));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @patientService.validateUserCanAccessPatient(#id, principal.id)")
    @Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request) {
        PatientResponse updatedPatient = patientService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Paciente actualizado exitosamente", updatedPatient));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar paciente", description = "Elimina un paciente del sistema")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Paciente eliminado exitosamente", null));
    }
}
