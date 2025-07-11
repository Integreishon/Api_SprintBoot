package com.hospital.backend.medical.repository;

import com.hospital.backend.enums.SeverityLevel;
import com.hospital.backend.medical.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con registros médicos
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Buscar registros médicos por doctor
     */
    Page<MedicalRecord> findByDoctorId(Long doctorId, Pageable pageable);
    
    /**
     * Buscar registros médicos por nivel de severidad
     */
    Page<MedicalRecord> findBySeverity(SeverityLevel severity, Pageable pageable);
    
    /**
     * Buscar registros médicos por rango de fechas
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.recordDate BETWEEN :startDate AND :endDate")
    Page<MedicalRecord> findByDateRange(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar registros médicos por paciente y rango de fechas
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.patient.id = :patientId AND mr.recordDate BETWEEN :startDate AND :endDate")
    Page<MedicalRecord> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar registros médicos por doctor y rango de fechas
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctor.id = :doctorId AND mr.recordDate BETWEEN :startDate AND :endDate")
    Page<MedicalRecord> findByDoctorIdAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Buscar registros médicos que requieren seguimiento entre fechas
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.followupRequired = true AND mr.followupDate BETWEEN :startDate AND :endDate")
    List<MedicalRecord> findByFollowupDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Contar registros médicos por doctor
     */
    long countByDoctorId(Long doctorId);
    
    /**
     * Buscar último registro médico de un paciente
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.patient.id = :patientId ORDER BY mr.recordDate DESC")
    Page<MedicalRecord> findLatestByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    /**
     * Buscar por diagnóstico (búsqueda parcial case insensitive)
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MedicalRecord> findByDiagnosisContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Buscar registro médico por ID de cita
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.id = :appointmentId")
    Optional<MedicalRecord> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    /**
     * Buscar registros médicos por ID de PACIENTE con filtros de fecha
     */
    @Query("SELECT mr FROM MedicalRecord mr JOIN mr.appointment a JOIN a.patient p " +
           "WHERE p.id = :patientId " +
           "AND (:startDate IS NULL OR mr.recordDate >= :startDate) " +
           "AND (:endDate IS NULL OR mr.recordDate <= :endDate)")
    List<MedicalRecord> findByPatientIdWithDateFilter(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Buscar registros médicos por doctor con filtros de fecha
     */
    @Query("SELECT mr FROM MedicalRecord mr JOIN mr.appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.appointmentDate <= :endDate)")
    List<MedicalRecord> findByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);
} 