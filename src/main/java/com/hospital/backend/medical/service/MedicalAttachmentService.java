package com.hospital.backend.medical.service;

import com.hospital.backend.medical.dto.request.CreateMedicalAttachmentRequest;
import com.hospital.backend.medical.dto.response.MedicalAttachmentResponse;
import com.hospital.backend.medical.entity.MedicalAttachment;
import com.hospital.backend.medical.entity.MedicalRecord;
import com.hospital.backend.medical.repository.MedicalAttachmentRepository;
import com.hospital.backend.medical.repository.MedicalRecordRepository;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.enums.FileType;
import com.hospital.backend.enums.UploadSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalAttachmentService {

    private final MedicalAttachmentRepository medicalAttachmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public MedicalAttachmentResponse create(CreateMedicalAttachmentRequest request) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new BusinessException("Registro médico no encontrado"));

        MedicalAttachment attachment = new MedicalAttachment();
        attachment.setMedicalRecord(medicalRecord);
        attachment.setPatient(medicalRecord.getPatient());
        attachment.setFileName(request.getFileName());
        attachment.setFileType(FileType.valueOf(request.getFileType()));
        attachment.setFileSize(request.getFileSize());
        attachment.setFilePath(request.getFileUrl());
        attachment.setContentType(determineContentType(request.getFileType()));
        attachment.setDescription(request.getDescription());
        attachment.setUploadDate(LocalDateTime.now());
        attachment.setUploadSource(UploadSource.DOCTOR);
        attachment.setIsPublic(false);
        
        MedicalAttachment savedAttachment = medicalAttachmentRepository.save(attachment);
        return mapToResponse(savedAttachment);
    }

    @Transactional(readOnly = true)
    public MedicalAttachmentResponse getById(Long id) {
        MedicalAttachment attachment = medicalAttachmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Archivo adjunto no encontrado"));
        return mapToResponse(attachment);
    }

    @Transactional(readOnly = true)
    public List<MedicalAttachmentResponse> getByMedicalRecord(Long medicalRecordId) {
        List<MedicalAttachment> attachments = medicalAttachmentRepository.findByMedicalRecordId(medicalRecordId);
        return attachments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        MedicalAttachment attachment = medicalAttachmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Archivo adjunto no encontrado"));
        medicalAttachmentRepository.delete(attachment);
    }

    private MedicalAttachmentResponse mapToResponse(MedicalAttachment attachment) {
        return MedicalAttachmentResponse.builder()
                .id(attachment.getId())
                .patientId(attachment.getPatient().getId())
                .patientName(attachment.getPatient().getFirstName() + " " + attachment.getPatient().getLastName())
                .medicalRecordId(attachment.getMedicalRecord() != null ? attachment.getMedicalRecord().getId() : null)
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .fileSize(attachment.getFileSize())
                .fileType(attachment.getFileType())
                .fileTypeName(attachment.getFileType().name())
                .contentType(attachment.getContentType())
                .uploadDate(attachment.getUploadDate())
                .uploadSource(attachment.getUploadSource())
                .uploadSourceName(attachment.getUploadSource().name())
                .description(attachment.getDescription())
                .tags(attachment.getTags())
                .isPublic(attachment.getIsPublic())
                .downloadUrl("/api/medical-attachments/" + attachment.getId() + "/download")
                .createdAt(attachment.getCreatedAt())
                .createdBy(attachment.getCreatedBy())
                .updatedAt(attachment.getUpdatedAt())
                .build();
    }

    private String determineContentType(String fileType) {
        return switch (FileType.valueOf(fileType)) {
            case PDF -> "application/pdf";
            case IMAGE -> "image/jpeg";
            case DOCUMENT -> "application/msword";
            case LAB_RESULT -> "application/pdf";
            case XRAY -> "image/dicom";
            case ULTRASOUND -> "image/dicom";
            case MRI -> "image/dicom";
            case CT_SCAN -> "image/dicom";
            case AUDIO -> "audio/mpeg";
            case VIDEO -> "video/mp4";
            case OTHER -> "application/octet-stream";
            default -> "application/octet-stream";
        };
    }
} 