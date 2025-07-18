package com.hospital.backend.user.repository;

import com.hospital.backend.enums.TimeBlock;
import com.hospital.backend.user.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operaciones con horarios de disponibilidad de doctores
 * Adaptado a la nueva lógica de bloques de tiempo
 */
@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    
    /**
     * Buscar horarios de un doctor por día de la semana
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isAvailable = :isAvailable")
    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndIsAvailable(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("isAvailable") Boolean isAvailable);
    
    /**
     * Buscar horarios de un doctor por día de la semana y bloque de tiempo
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.timeBlock = :timeBlock AND " +
           "da.isAvailable = :isAvailable")
    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndTimeBlockAndIsAvailable(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("timeBlock") TimeBlock timeBlock,
            @Param("isAvailable") Boolean isAvailable);
    
    /**
     * Buscar todos los horarios disponibles de un doctor
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.isAvailable = true " +
           "ORDER BY da.dayOfWeek ASC, da.timeBlock ASC")
    List<DoctorAvailability> findAvailableByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Buscar todos los horarios de un doctor
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId " +
           "ORDER BY da.dayOfWeek ASC, da.timeBlock ASC")
    List<DoctorAvailability> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Verificar si un doctor trabaja en un día específico
     */
    @Query("SELECT COUNT(da) > 0 FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isAvailable = true")
    boolean existsByDoctorIdAndDayOfWeekAndIsAvailable(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Eliminar horarios de un doctor específico
     */
    void deleteByDoctorId(Long doctorId);
    
    /**
     * Contar bloques disponibles para un día de la semana
     */
    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isAvailable = true")
    Long countAvailableBlocksForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Contar slots disponibles para un día (compatibilidad con dashboard)
     */
    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE " +
           "da.dayOfWeek = :dayOfWeek AND " +
           "da.isAvailable = true")
    Long countAvailableSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Contar total de slots para un día (compatibilidad con dashboard)
     */
    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE " +
           "da.dayOfWeek = :dayOfWeek")
    Long countTotalSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    /**
     * Buscar horarios por doctor y día
     */
    @Query("SELECT da FROM DoctorAvailability da WHERE " +
           "da.doctor.id = :doctorId AND " +
           "da.dayOfWeek = :dayOfWeek")
    List<DoctorAvailability> findByDoctorIdAndDayOfWeek(@Param("doctorId") Long doctorId, @Param("dayOfWeek") Integer dayOfWeek);
}
