package com.hospital.backend.medical.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.medical.dto.request.CreateMedicalAttachmentRequest;
import com.hospital.backend.medical.dto.response.MedicalAttachmentResponse;
import com.hospital.backend.medical.service.MedicalAttachmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/medical-attachments")
@RequiredArgsConstructor
@Tag(name = "📁 Archivos Médicos", description = "Gestión de archivos adjuntos y documentos médicos.")
public class MedicalAttachmentController {

    private final MedicalAttachmentService medicalAttachmentService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Subir archivo médico", description = "Sube un archivo adjunto a un historial médico")
    public ResponseEntity<ApiResponse<MedicalAttachmentResponse>> create(
            @Valid @RequestBody CreateMedicalAttachmentRequest request) {
        MedicalAttachmentResponse response = medicalAttachmentService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Archivo adjunto creado exitosamente", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(summary = "Obtener archivo por ID", description = "Recupera un archivo médico específico")
    public ResponseEntity<ApiResponse<MedicalAttachmentResponse>> getById(@PathVariable Long id) {
        MedicalAttachmentResponse response = medicalAttachmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Operation(summary = "Archivos por historial", description = "Obtiene todos los archivos de un historial médico")
    public ResponseEntity<ApiResponse<List<MedicalAttachmentResponse>>> getByMedicalRecord(
            @PathVariable Long medicalRecordId) {
        List<MedicalAttachmentResponse> response = medicalAttachmentService.getByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Eliminar archivo médico", description = "Elimina un archivo adjunto del sistema")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        medicalAttachmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Archivo adjunto eliminado exitosamente"));
    }
} 