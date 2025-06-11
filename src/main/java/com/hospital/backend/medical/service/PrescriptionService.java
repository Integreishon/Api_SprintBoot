package com.hospital.backend.medical.service;

import com.hospital.backend.medical.dto.request.CreatePrescriptionRequest;
import com.hospital.backend.medical.dto.response.PrescriptionResponse;
import com.hospital.backend.medical.entity.Prescription;
import com.hospital.backend.medical.entity.MedicalRecord;
import com.hospital.backend.medical.repository.PrescriptionRepository;
import com.hospital.backend.medical.repository.MedicalRecordRepository;
import com.hospital.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public PrescriptionResponse create(CreatePrescriptionRequest request) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new BusinessException("Registro médico no encontrado"));

        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(medicalRecord);
        prescription.setPatient(medicalRecord.getPatient());
        prescription.setDoctor(medicalRecord.getDoctor());
        prescription.setIssueDate(LocalDateTime.now());
        prescription.setMedicationName(request.getMedication());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());
        prescription.setActive(true);
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return mapToResponse(savedPrescription);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Prescripción no encontrada"));
        return mapToResponse(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getByPatient(Long patientId, String startDate, String endDate) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId, startDate, endDate);
        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getByMedicalRecord(Long medicalRecordId) {
        List<Prescription> prescriptions = prescriptionRepository.findByMedicalRecordId(medicalRecordId);
        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generatePdf(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Prescripción no encontrada"));
        log.debug("Generando PDF para prescripción: {}", prescription.getId());
        
        // TODO: Implementar generación de PDF
        byte[] pdfContent = new byte[0]; // Placeholder
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient().getId())
                .patientName(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName())
                .doctorId(prescription.getDoctor().getId())
                .doctorName(prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName())
                .medicalRecordId(prescription.getMedicalRecord().getId())
                .issueDate(prescription.getIssueDate())
                .expiryDate(prescription.getExpiryDate())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .instructions(prescription.getInstructions())
                .notes(prescription.getNotes())
                .quantity(prescription.getQuantity())
                .refills(prescription.getRefills())
                .active(prescription.getActive())
                .dispensed(prescription.getDispensed())
                .dispensedDate(prescription.getDispensedDate())
                .dispensedBy(prescription.getDispensedBy())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }
} 