// Repository para disponibilidad horaria de doctores
package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    
    List<DoctorAvailability> findByDoctorId(Long doctorId);
    
    List<DoctorAvailability> findByDoctorIdAndIsActive(Long doctorId, Boolean isActive);
    
    List<DoctorAvailability> findByDayOfWeek(Integer dayOfWeek);
    
    List<DoctorAvailability> findByDayOfWeekAndIsActive(Integer dayOfWeek, Boolean isActive);
    
    Optional<DoctorAvailability> findByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek);
    
    @Query("SELECT da FROM DoctorAvailability da " +
           "WHERE da.doctor.id = :doctorId AND da.dayOfWeek = :dayOfWeek AND da.isActive = true")
    Optional<DoctorAvailability> findActivByDoctorIdAndDayOfWeek(@Param("doctorId") Long doctorId, 
                                                                @Param("dayOfWeek") Integer dayOfWeek);
    
    @Query("SELECT da FROM DoctorAvailability da " +
           "WHERE da.doctor.id = :doctorId " +
           "AND da.dayOfWeek = :dayOfWeek " +
           "AND da.startTime <= :time " +
           "AND da.endTime > :time " +
           "AND da.isActive = true")
    Optional<DoctorAvailability> findByDoctorAndDayAndTime(@Param("doctorId") Long doctorId,
                                                          @Param("dayOfWeek") Integer dayOfWeek,
                                                          @Param("time") LocalTime time);
    
    @Query("SELECT da FROM DoctorAvailability da " +
           "WHERE da.doctor.id IN :doctorIds AND da.dayOfWeek = :dayOfWeek AND da.isActive = true")
    List<DoctorAvailability> findByDoctorIdsAndDayOfWeek(@Param("doctorIds") List<Long> doctorIds,
                                                        @Param("dayOfWeek") Integer dayOfWeek);
    
    @Query("SELECT DISTINCT da.doctor.id FROM DoctorAvailability da " +
           "WHERE da.dayOfWeek = :dayOfWeek " +
           "AND da.startTime <= :time " +
           "AND da.endTime > :time " +
           "AND da.isActive = true")
    List<Long> findAvailableDoctorIds(@Param("dayOfWeek") Integer dayOfWeek, @Param("time") LocalTime time);
    
    @Query("SELECT COUNT(da) FROM DoctorAvailability da WHERE da.doctor.id = :doctorId AND da.isActive = true")
    long countActivByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT COUNT(DISTINCT da.doctor.id) FROM DoctorAvailability da " +
           "WHERE da.dayOfWeek = :dayOfWeek AND da.isActive = true")
    long countAvailableDoctorsByDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    boolean existsByDoctorIdAndDayOfWeekAndIsActive(Long doctorId, Integer dayOfWeek, Boolean isActive);
    
    void deleteByDoctorId(Long doctorId);
    
    void deleteByDoctorIdAndDayOfWeek(Long doctorId, Integer dayOfWeek);
    
    @Query("SELECT COUNT(da) FROM DoctorAvailability da " +
           "WHERE da.dayOfWeek = :dayOfWeek " +
           "AND da.isActive = true")
    Long countAvailableSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
    
    @Query("SELECT COUNT(da) FROM DoctorAvailability da " +
           "WHERE da.dayOfWeek = :dayOfWeek")
    Long countTotalSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);
}