package com.hospital.backend.medical.dto.response;

import com.hospital.backend.enums.FileType;
import com.hospital.backend.enums.UploadSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de un archivo médico adjunto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalAttachmentResponse {

    private Long id;
    
    // Datos del paciente
    private Long patientId;
    private String patientName;
    
    // Datos del registro médico
    private Long medicalRecordId;
    
    // Datos del archivo
    private String fileName;
    private String filePath;
    private Long fileSize;
    private FileType fileType;
    private String fileTypeName;
    private String contentType;
    
    // Información de carga
    private LocalDateTime uploadDate;
    private UploadSource uploadSource;
    private String uploadSourceName;
    
    // Metadatos del archivo
    private String description;
    private String tags;
    private Boolean isPublic;
    
    // URL para descarga
    private String downloadUrl;
    
    // Metadatos
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
} 