package com.hospital.backend.appointment.repository;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones CRUD y consultas personalizadas de citas
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Buscar citas por paciente
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    
    // Buscar citas por doctor
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    
    // Buscar citas por fecha
    Page<Appointment> findByAppointmentDate(LocalDate date, Pageable pageable);
    
    // Buscar citas por especialidad
    Page<Appointment> findBySpecialtyId(Long specialtyId, Pageable pageable);
    
    // Buscar citas por estado
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    
    // Buscar citas por fecha y doctor
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    
    // Buscar citas por fecha y paciente
    List<Appointment> findByPatientIdAndAppointmentDate(Long patientId, LocalDate date);
    
    // Verificar si un doctor tiene disponibilidad en un horario específico
    boolean existsByDoctorIdAndAppointmentDateAndStartTimeBetween(
            Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    // Buscar citas por rango de fechas
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Contar citas por doctor y estado
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = :status")
    Long countByDoctorIdAndStatus(@Param("doctorId") Long doctorId, @Param("status") AppointmentStatus status);
    
    // Contar citas por especialidad y estado
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.specialty.id = :specialtyId AND a.status = :status")
    Long countBySpecialtyIdAndStatus(@Param("specialtyId") Long specialtyId, 
                                     @Param("status") AppointmentStatus status);
    
    // Buscar citas por paciente y rango de fechas
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.appointmentDate, a.startTime")
    List<Appointment> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    // Buscar citas por doctor en un día específico, con estado específico
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.status IN :statuses " +
           "ORDER BY a.startTime")
    List<Appointment> findByDoctorIdAndDateAndStatusIn(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("statuses") List<AppointmentStatus> statuses);
    
    // Buscar citas pendientes (programadas o confirmadas)
    @Query("SELECT a FROM Appointment a WHERE a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND a.appointmentDate >= :today " +
           "ORDER BY a.appointmentDate, a.startTime")
    Page<Appointment> findPendingAppointments(@Param("today") LocalDate today, Pageable pageable);
    
    // Buscar citas que necesitan recordatorio
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :tomorrow " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "AND (a.reminderSent = false OR a.reminderSent IS NULL)")
    List<Appointment> findAppointmentsNeedingReminders(@Param("tomorrow") LocalDate tomorrow);

    Long countByStatus(AppointmentStatus status);
    
    Long countByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE a.appointmentDate BETWEEN :start AND :end")
    Long countDistinctPatientByAppointmentDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT DATE(a.appointmentDate) as date, COUNT(a) as count " +
           "FROM Appointment a " +
           "WHERE a.appointmentDate >= :startDate " +
           "GROUP BY DATE(a.appointmentDate) " +
           "ORDER BY date")
    List<Object[]> countAppointmentsGroupByDayForLastWeek();
    
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, " +
           "COUNT(a) as appointmentsCount, " +
           "SUM(a.price) as revenue " +
           "FROM Appointment a " +
           "JOIN a.doctor d " +
           "JOIN d.specialties ds " +
           "JOIN ds.specialty s " +
           "WHERE ds.isPrimary = true " +
           "GROUP BY s.id, s.name " +
           "ORDER BY appointmentsCount DESC")
    List<Object[]> findTopSpecialties(@Param("limit") int limit);
    
    @Query("SELECT d.id as doctorId, CONCAT(d.firstName, ' ', d.lastName) as doctorName, " +
           "ds.specialty.name as specialtyName, COUNT(a) as appointmentsCount, " +
           "SUM(a.price) as revenue, " +
           "AVG(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as rating " +
           "FROM Appointment a " +
           "JOIN a.doctor d " +
           "JOIN d.specialties ds " +
           "WHERE ds.isPrimary = true " +
           "GROUP BY d.id, d.firstName, d.lastName, ds.specialty.name " +
           "ORDER BY appointmentsCount DESC")
    List<Object[]> findTopDoctors(@Param("limit") int limit);
} 