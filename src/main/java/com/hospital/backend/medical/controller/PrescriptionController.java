package com.hospital.backend.medical.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.medical.dto.request.CreatePrescriptionRequest;
import com.hospital.backend.medical.dto.response.PrescriptionResponse;
import com.hospital.backend.medical.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> create(
            @Valid @RequestBody CreatePrescriptionRequest request) {
        PrescriptionResponse response = prescriptionService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Prescripción creada exitosamente", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PrescriptionResponse>> getById(@PathVariable Long id) {
        PrescriptionResponse response = prescriptionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<PrescriptionResponse> response = prescriptionService.getByPatient(patientId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<PrescriptionResponse>>> getByMedicalRecord(
            @PathVariable Long medicalRecordId) {
        List<PrescriptionResponse> response = prescriptionService.getByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        return prescriptionService.generatePdf(id);
    }
} 