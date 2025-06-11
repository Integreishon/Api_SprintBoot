package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Repositorio para la gestión de horarios de doctores
 */
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    /**
     * Busca los horarios de un doctor para un día específico de la semana
     * 
     * @param doctorId ID del doctor
     * @param dayOfWeek Día de la semana
     * @return Lista de horarios encontrados
     */
    List<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
    
    /**
     * Verifica si un doctor tiene horario asignado para un día específico
     * 
     * @param doctorId ID del doctor
     * @param dayOfWeek Día de la semana
     * @return true si existe al menos un horario, false en caso contrario
     */
    boolean existsByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
    
    /**
     * Busca todos los horarios de un doctor
     * 
     * @param doctorId ID del doctor
     * @return Lista de horarios encontrados
     */
    List<DoctorSchedule> findByDoctorId(Long doctorId);
    
    /**
     * Busca horarios activos para un doctor
     * 
     * @param doctorId ID del doctor
     * @param active Estado activo/inactivo
     * @return Lista de horarios encontrados
     */
    List<DoctorSchedule> findByDoctorIdAndActive(Long doctorId, Boolean active);
} 