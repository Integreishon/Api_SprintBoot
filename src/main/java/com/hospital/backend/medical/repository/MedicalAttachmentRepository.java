package com.hospital.backend.medical.repository;

import com.hospital.backend.enums.FileType;
import com.hospital.backend.enums.UploadSource;
import com.hospital.backend.medical.entity.MedicalAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones con archivos médicos adjuntos
 */
@Repository
public interface MedicalAttachmentRepository extends JpaRepository<MedicalAttachment, Long> {

    /**
     * Buscar archivos por paciente
     */
    @Query("SELECT ma FROM MedicalAttachment ma WHERE ma.medicalRecord.appointment.patient.id = :patientId")
    Page<MedicalAttachment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    List<MedicalAttachment> findByMedicalRecordId(Long medicalRecordId);
    

    Page<MedicalAttachment> findByFileType(FileType fileType, Pageable pageable);
    
    Page<MedicalAttachment> findByUploadSource(UploadSource uploadSource, Pageable pageable);
 
    Page<MedicalAttachment> findByIsPublicTrue(Pageable pageable);
    
    /**
     * Buscar archivos por rango de fechas de carga
     */
    @Query("SELECT ma FROM MedicalAttachment ma WHERE ma.uploadDate BETWEEN :startDate AND :endDate")
    Page<MedicalAttachment> findByUploadDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar archivos por paciente y tipo
     */
    @Query("SELECT ma FROM MedicalAttachment ma WHERE ma.medicalRecord.appointment.patient.id = :patientId AND ma.fileType = :fileType")
    Page<MedicalAttachment> findByPatientIdAndFileType(@Param("patientId") Long patientId, @Param("fileType") FileType fileType, Pageable pageable);
    
    /**
     * Buscar archivos por nombre (parcial case insensitive)
     */
    @Query("SELECT ma FROM MedicalAttachment ma WHERE LOWER(ma.fileName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MedicalAttachment> findByFileNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Buscar archivos por etiquetas (parcial case insensitive)
     */
    @Query("SELECT ma FROM MedicalAttachment ma WHERE LOWER(ma.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MedicalAttachment> findByTagsContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Contar archivos por paciente
     */
    @Query("SELECT COUNT(ma) FROM MedicalAttachment ma WHERE ma.medicalRecord.appointment.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Contar archivos por tipo
     */
    long countByFileType(FileType fileType);
   
    long countByUploadSource(UploadSource uploadSource);

    @Query("SELECT ma FROM MedicalAttachment ma WHERE ma.medicalRecord.appointment.patient.id = :patientId AND ma.fileType = :fileType AND ma.uploadDate BETWEEN :startDate AND :endDate")
    Page<MedicalAttachment> findByPatientIdAndFileTypeAndUploadDateBetween(
            @Param("patientId") Long patientId,
            @Param("fileType") FileType fileType,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
} 