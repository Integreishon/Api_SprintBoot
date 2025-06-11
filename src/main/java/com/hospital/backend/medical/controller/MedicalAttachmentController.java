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

@RestController
@RequestMapping("/medical-attachments")
@RequiredArgsConstructor
public class MedicalAttachmentController {

    private final MedicalAttachmentService medicalAttachmentService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalAttachmentResponse>> create(
            @Valid @RequestBody CreateMedicalAttachmentRequest request) {
        MedicalAttachmentResponse response = medicalAttachmentService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Archivo adjunto creado exitosamente", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalAttachmentResponse>> getById(@PathVariable Long id) {
        MedicalAttachmentResponse response = medicalAttachmentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<MedicalAttachmentResponse>>> getByMedicalRecord(
            @PathVariable Long medicalRecordId) {
        List<MedicalAttachmentResponse> response = medicalAttachmentService.getByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        medicalAttachmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Archivo adjunto eliminado exitosamente"));
    }
} 