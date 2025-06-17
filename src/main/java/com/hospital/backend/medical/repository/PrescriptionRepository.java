package com.hospital.backend.medical.repository;

import com.hospital.backend.medical.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para operaciones con recetas médicas
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Buscar recetas por paciente
     */
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId " +
           "AND (:startDate IS NULL OR p.issueDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.issueDate <= :endDate)")
    List<Prescription> findByPatientId(
            @Param("patientId") Long patientId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
    
    /**
     * Buscar recetas por registro médico
     */
    @Query("SELECT p FROM Prescription p WHERE p.medicalRecord.id = :medicalRecordId")
    List<Prescription> findByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);
    
    /**
     * Buscar recetas activas
     */
    Page<Prescription> findByActiveTrue(Pageable pageable);
    
    /**
     * Buscar recetas activas por paciente
     */
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND p.active = true")
    List<Prescription> findActiveByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Buscar recetas por rango de fechas de emisión
     */
    @Query("SELECT p FROM Prescription p WHERE p.issueDate BETWEEN :startDate AND :endDate")
    Page<Prescription> findByIssueDateBetween(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            Pageable pageable);
    
    /**
     * Buscar recetas por nombre de medicamento (parcial case insensitive)
     */
    @Query("SELECT p FROM Prescription p WHERE LOWER(p.medicationName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Prescription> findByMedicationNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Contar recetas por paciente
     */
    long countByPatientId(Long patientId);
    
    /**
     * Contar recetas por doctor
     */
    long countByDoctorId(Long doctorId);
    
    /**
     * Contar recetas por medicamento
     */
    @Query("SELECT COUNT(p) FROM Prescription p WHERE LOWER(p.medicationName) LIKE LOWER(CONCAT('%', :medicationName, '%'))")
    long countByMedicationName(@Param("medicationName") String medicationName);
} 