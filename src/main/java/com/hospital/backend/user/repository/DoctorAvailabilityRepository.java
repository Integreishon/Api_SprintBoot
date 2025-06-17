package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

/**
 * Repository para operaciones con horarios de disponibilidad de doctores
 */
@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    
    /**
     * Buscar horarios de un doctor por día de la semana
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isActive = :isActive")
    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndIsActive(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("isActive") Boolean isActive);
    
    /**
     * Buscar todos los horarios activos de un doctor
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.isActive = true " +
           "ORDER BY da.dayOfWeek ASC, da.startTime ASC")
    List<DoctorAvailability> findActiveByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Buscar todos los horarios de un doctor
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId " +
           "ORDER BY da.dayOfWeek ASC, da.startTime ASC")
    List<DoctorAvailability> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Verificar si un doctor trabaja en un día específico
     */
    @Query("SELECT COUNT(da) > 0 FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isActive = true")
    boolean existsByDoctorIdAndDayOfWeekAndIsActive(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Buscar horarios que cubren una hora específica
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isActive = true AND " +
           "da.startTime <= :time AND da.endTime > :time")
    List<DoctorAvailability> findByDoctorAndDayAndTimeRange(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("time") LocalTime time);
    
    /**
     * Eliminar horarios de un doctor específico
     */
    void deleteByDoctorId(Long doctorId);
    
    /**
     * Contar slots disponibles para un día de la semana (versión simplificada)
     */
    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isActive = true")
    Long countAvailableSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Contar total de slots para un día de la semana (versión simplificada)
     */
    @Query("SELECT COUNT(da) * 10 FROM DoctorAvailability da WHERE " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isActive = true")
    Long countTotalSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Buscar horarios por doctor y día
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek")
    List<DoctorAvailability> findByDoctorIdAndDayOfWeek(@Param("doctorId") Long doctorId, @Param("dayOfWeek") Integer dayOfWeek);
}
