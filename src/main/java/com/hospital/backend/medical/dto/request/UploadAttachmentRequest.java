package com.hospital.backend.medical.dto.request;

import com.hospital.backend.enums.FileType;
import com.hospital.backend.enums.UploadSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de carga de un archivo médico
 * (los datos del archivo binario se manejan por MultipartFile en el controlador)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadAttachmentRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long patientId;

    private Long medicalRecordId;

    @NotBlank(message = "El nombre del archivo es obligatorio")
    @Size(min = 2, max = 255, message = "El nombre del archivo debe tener entre 2 y 255 caracteres")
    private String fileName;

    @NotNull(message = "El tipo de archivo es obligatorio")
    private FileType fileType;

    @NotNull(message = "El origen de carga es obligatorio")
    private UploadSource uploadSource;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres")
    private String description;

    private String tags;

    private Boolean isPublic = false;
} 