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
import com.hospital.backend.auth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;

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
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Obtener perfil del paciente autenticado", description = "Recupera el perfil del paciente que ha iniciado sesión actualmente.")
    public ResponseEntity<ApiResponse<PatientResponse>> getAuthenticatedPatientProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        PatientResponse patient = patientService.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Perfil del paciente recuperado exitosamente", patient));
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
    
    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Buscar paciente por DNI", description = "Busca un paciente por su número de DNI")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByDni(
            @PathVariable String dni) {
        PatientResponse patient = patientService.findByDni(dni);
        return ResponseEntity.ok(ApiResponse.success("Paciente encontrado", patient));
    }

    @GetMapping("/byUserId/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and #userId == principal.id)")
    @Operation(summary = "Buscar paciente por ID de usuario", description = "Busca un paciente por su ID de usuario asociado. Un paciente solo puede consultar su propia información.")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByUserId(@PathVariable Long userId) {
        PatientResponse patient = patientService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Paciente encontrado por ID de usuario", patient));
    }

    @GetMapping("/check-dni/{dni}")
    @Operation(summary = "Verificar si un DNI existe", description = "Verifica si un DNI ya está registrado en el sistema")
    public ResponseEntity<Map<String, Boolean>> checkDniExists(
            @PathVariable String dni) {
        boolean exists = patientService.existsByDni(dni);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
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
    
    @PostMapping("/register")
    @Operation(summary = "Registro público de paciente", description = "Permite a un usuario registrarse como paciente sin autenticación previa")
    public ResponseEntity<ApiResponse<PatientResponse>> registerPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        PatientResponse createdPatient = patientService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registro exitoso", createdPatient));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Actualizar perfil del paciente autenticado", description = "Actualiza los datos del perfil del paciente que ha iniciado sesión.")
    public ResponseEntity<ApiResponse<PatientResponse>> updateAuthenticatedPatientProfile(
            @Valid @RequestBody UpdatePatientRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        PatientResponse updatedPatient = patientService.updateByUserId(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado exitosamente", updatedPatient));
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
