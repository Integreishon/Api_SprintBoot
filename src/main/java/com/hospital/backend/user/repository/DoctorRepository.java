// Repository para gestión de doctores
package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByUserId(Long userId);
    
    Optional<Doctor> findByUserEmail(String email);
    
    Optional<Doctor> findByCmpNumber(String cmpNumber);
    
    boolean existsByCmpNumber(String cmpNumber);
    
    boolean existsByUserEmail(String email);
    
    Page<Doctor> findByIsActive(Boolean isActive, Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Doctor> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT d FROM Doctor d WHERE d.cmpNumber LIKE %:cmpNumber%")
    Page<Doctor> findByCmpNumberContaining(@Param("cmpNumber") String cmpNumber, Pageable pageable);
    
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.specialties ds " +
           "WHERE ds.specialty.id = :specialtyId AND d.isActive = true")
    List<Doctor> findBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.specialties ds " +
           "WHERE ds.specialty.id = :specialtyId AND d.isActive = true")
    Page<Doctor> findBySpecialtyId(@Param("specialtyId") Long specialtyId, Pageable pageable);
    
    @Query("SELECT DISTINCT d FROM Doctor d " +
           "JOIN d.specialties ds " +
           "WHERE ds.specialty.name LIKE %:specialtyName% AND d.isActive = true")
    Page<Doctor> findBySpecialtyNameContaining(@Param("specialtyName") String specialtyName, Pageable pageable);
    
    @Query("SELECT d FROM Doctor d " +
           "WHERE (:name IS NULL OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:specialtyId IS NULL OR d.id IN (SELECT ds.doctor.id FROM DoctorSpecialty ds WHERE ds.specialty.id = :specialtyId)) " +
           "AND (:isActive IS NULL OR d.isActive = :isActive)")
    Page<Doctor> findByFilters(@Param("name") String name,
                             @Param("specialtyId") Long specialtyId,
                             @Param("isActive") Boolean isActive,
                             Pageable pageable);
    
    @Query("SELECT d FROM Doctor d " +
           "JOIN d.availability da " +
           "WHERE da.dayOfWeek = :dayOfWeek AND da.isActive = true AND d.isActive = true")
    List<Doctor> findAvailableByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive = true")
    long countActiveDoctors();
    
    @Query("SELECT COUNT(DISTINCT d) FROM Doctor d JOIN d.specialties ds WHERE ds.specialty.id = :specialtyId")
    long countBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    @Query("SELECT d FROM Doctor d WHERE d.hireDate BETWEEN :startDate AND :endDate")
    List<Doctor> findByHireDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}