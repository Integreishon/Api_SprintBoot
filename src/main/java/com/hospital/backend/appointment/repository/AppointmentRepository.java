package com.hospital.backend.appointment.repository;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.TimeBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operaciones de base de datos de citas médicas
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // =========================
    // Verificación de disponibilidad
    // =========================
    
    /**
     * Verificar si un doctor tiene cita en una fecha y bloque específico
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.timeBlock = :timeBlock AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS')")
    boolean existsByDoctorAndDateAndTimeBlock(@Param("doctorId") Long doctorId,
                                        @Param("date") LocalDate date,
                                        @Param("timeBlock") TimeBlock timeBlock);
    
    /**
     * Buscar citas conflictivas de un doctor en una fecha y bloque
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.timeBlock = :timeBlock AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS')")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("timeBlock") TimeBlock timeBlock);
    
    /**
     * Contar citas por doctor, fecha y bloque de tiempo
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :date AND " +
           "a.timeBlock = :timeBlock AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS')")
    int countByDoctorIdAndAppointmentDateAndTimeBlock(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("timeBlock") TimeBlock timeBlock);
    
    // =========================
    // Consultas por entidades
    // =========================
    
    /**
     * Buscar citas por paciente
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.appointmentDate DESC")
    Page<Appointment> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);
    
    /**
     * Buscar citas por paciente ordenadas por fecha descendente
     * Usado en el portal virtual para mostrar el historial de citas al paciente
     */
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    
    /**
     * Buscar citas por doctor
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId ORDER BY a.appointmentDate ASC")
    Page<Appointment> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
    
    /**
     * Buscar citas por especialidad
     */
    @Query("SELECT a FROM Appointment a WHERE a.specialty.id = :specialtyId ORDER BY a.appointmentDate ASC")
    Page<Appointment> findBySpecialtyId(@Param("specialtyId") Long specialtyId, Pageable pageable);
    
    // =========================
    // Consultas por fecha
    // =========================
    
    /**
     * Buscar citas de un doctor en una fecha específica
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date ORDER BY a.timeBlock")
    List<Appointment> findByDoctorIdAndAppointmentDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
    
    /**
     * Buscar citas por fecha y bloque de tiempo
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.timeBlock = :timeBlock ORDER BY a.doctor.id")
    List<Appointment> findByAppointmentDateAndTimeBlock(@Param("date") LocalDate date, @Param("timeBlock") TimeBlock timeBlock);
    
    /**
     * Buscar citas de un paciente en un rango de fechas
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.patient.id = :patientId AND " +
           "a.appointmentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.appointmentDate DESC")
    List<Appointment> findByPatientAndDateRange(@Param("patientId") Long patientId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    // =========================
    // Consultas por estado
    // =========================
    
    /**
     * Buscar citas por estado
     */
    @Query("SELECT a FROM Appointment a WHERE a.status = :status ORDER BY a.appointmentDate ASC")
    Page<Appointment> findByStatus(@Param("status") AppointmentStatus status, Pageable pageable);
    
    /**
     * Buscar citas pendientes de un doctor (scheduled o in_progress)
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS') " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findPendingAppointmentsByDoctor(@Param("doctorId") Long doctorId);
    
    /**
     * Buscar citas pendientes de un paciente
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.patient.id = :patientId AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS') " +
           "ORDER BY a.appointmentDate ASC")
    List<Appointment> findPendingAppointmentsByPatient(@Param("patientId") Long patientId);
    
    // =========================
    // Estadísticas y reportes
    // =========================
    
    /**
     * Contar citas por doctor en un rango de fechas
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByDoctorAndDateRange(@Param("doctorId") Long doctorId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
    
    /**
     * Contar citas por estado en un rango de fechas
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
           "a.status = :status AND " +
           "a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndAppointmentDateBetween(@Param("status") AppointmentStatus status,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
    
    /**
     * Sumar ingresos por fecha (solo citas completadas)
     */
    @Query("SELECT COALESCE(SUM(a.specialty.consultationPrice), 0) FROM Appointment a WHERE " +
           "a.status = 'COMPLETED' AND " +
           "a.appointmentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    // =========================
    // Búsquedas específicas
    // =========================
    
    /**
     * Buscar cita por ID con todas las relaciones cargadas
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.specialty s " +
           "WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Buscar citas que necesitan recordatorio (para notificaciones)
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.appointmentDate = :tomorrow AND " +
           "(a.status = 'SCHEDULED' OR a.status = 'IN_PROGRESS')")
    List<Appointment> findAppointmentsForReminder(@Param("tomorrow") LocalDate tomorrow);
    
    // =========================
    // Métodos de consulta adicionales
    // =========================
    
    /**
     * Verificar si existe una cita
     */
    boolean existsById(Long id);
    
    /**
     * Buscar citas por múltiples estados
     */
    @Query("SELECT a FROM Appointment a WHERE a.status IN :statuses ORDER BY a.appointmentDate ASC")
    List<Appointment> findByStatusIn(@Param("statuses") List<AppointmentStatus> statuses);
    
    /**
     * Buscar citas de hoy por doctor
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "a.doctor.id = :doctorId AND " +
           "a.appointmentDate = :today " +
           "ORDER BY a.timeBlock")
    List<Appointment> findTodayAppointmentsByDoctor(@Param("doctorId") Long doctorId, 
                                                   @Param("today") LocalDate today);
    
    // =========================
    // Métodos para Dashboard
    // =========================
    
    /**
     * Contar citas por estado
     */
    Long countByStatus(AppointmentStatus status);
    
    /**
     * Contar citas en un rango de fechas
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
           "a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByAppointmentDateBetween(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * Contar pacientes únicos en un rango de fechas
     */
    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE " +
           "a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countDistinctPatientByAppointmentDateBetween(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Obtener conteo de citas por día para la última semana
     */
    @Query("SELECT a.appointmentDate, COUNT(a) FROM Appointment a WHERE " +
           "a.appointmentDate >= :startDate " +
           "GROUP BY a.appointmentDate " +
           "ORDER BY a.appointmentDate")
    List<Object[]> countAppointmentsGroupByDayForLastWeek(@Param("startDate") LocalDate startDate);
    
    /**
     * Obtener top especialidades por número de citas
     */
    @Query("SELECT s.id, s.name, COUNT(a), COALESCE(SUM(s.consultationPrice), 0) " +
           "FROM Appointment a JOIN a.specialty s " +
           "WHERE a.status = 'COMPLETED' " +
           "GROUP BY s.id, s.name " +
           "ORDER BY COUNT(a) DESC " +
           "LIMIT :limit")
    List<Object[]> findTopSpecialities(@Param("limit") int limit);
    
    /**
     * Obtener top doctores por número de citas
     */
    @Query("SELECT d.id, CONCAT(d.firstName, ' ', d.lastName), " +
           "s.name, " +
           "COUNT(a), COALESCE(SUM(s.consultationPrice), 0) " +
           "FROM Appointment a JOIN a.doctor d JOIN a.specialty s " +
           "WHERE a.status = 'COMPLETED' " +
           "GROUP BY d.id, d.firstName, d.lastName, s.name " +
           "ORDER BY COUNT(a) DESC " +
           "LIMIT :limit")
    List<Object[]> findTopDoctors(@Param("limit") int limit);
}
