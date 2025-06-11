package com.hospital.backend.medical.controller;

import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.medical.dto.request.CreateMedicalRecordRequest;
import com.hospital.backend.medical.dto.response.MedicalRecordResponse;
import com.hospital.backend.medical.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> create(
            @Valid @RequestBody CreateMedicalRecordRequest request) {
        MedicalRecordResponse response = medicalRecordService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Registro médico creado exitosamente", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getById(@PathVariable Long id) {
        MedicalRecordResponse response = medicalRecordService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<MedicalRecordResponse> response = medicalRecordService.getByPatient(patientId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<MedicalRecordResponse> response = medicalRecordService.getByDoctor(doctorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> getByAppointment(
            @PathVariable Long appointmentId) {
        MedicalRecordResponse response = medicalRecordService.getByAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 