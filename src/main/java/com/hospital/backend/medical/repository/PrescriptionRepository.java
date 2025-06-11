package com.hospital.backend.medical.repository;

import com.hospital.backend.medical.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones con recetas médicas
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Buscar recetas por paciente
     */
    @Query("SELECT p FROM Prescription p JOIN p.medicalRecord mr JOIN mr.appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.appointmentDate <= :endDate)")
    List<Prescription> findByPatientId(
            @Param("patientId") Long patientId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
    
    /**
     * Buscar recetas por doctor
     */
    @Query("SELECT p FROM Prescription p JOIN p.medicalRecord mr JOIN mr.appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.appointmentDate <= :endDate)")
    List<Prescription> findByDoctorId(
            @Param("doctorId") Long doctorId,
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
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar recetas por nombre de medicamento (parcial case insensitive)
     */
    @Query("SELECT p FROM Prescription p WHERE LOWER(p.medicationName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Prescription> findByMedicationNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Buscar recetas que expiran pronto
     */
    @Query("SELECT p FROM Prescription p WHERE p.active = true AND p.expiryDate BETWEEN :startDate AND :endDate")
    List<Prescription> findExpiringPrescriptions(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
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
    
    /**
     * Buscar recetas por doctor y rango de fechas
     */
    @Query("SELECT p FROM Prescription p WHERE p.doctor.id = :doctorId AND p.issueDate BETWEEN :startDate AND :endDate")
    Page<Prescription> findByDoctorIdAndIssueDateBetween(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
} 