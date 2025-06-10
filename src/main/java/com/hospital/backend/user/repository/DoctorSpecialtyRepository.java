// Repository para relación doctor-especialidad
package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.DoctorSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorSpecialtyRepository extends JpaRepository<DoctorSpecialty, Long> {
    
    List<DoctorSpecialty> findByDoctorId(Long doctorId);
    
    List<DoctorSpecialty> findBySpecialtyId(Long specialtyId);
    
    Optional<DoctorSpecialty> findByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);
    
    boolean existsByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);
    
    @Query("SELECT ds FROM DoctorSpecialty ds WHERE ds.doctor.id = :doctorId AND ds.isPrimary = true")
    Optional<DoctorSpecialty> findPrimaryByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT ds FROM DoctorSpecialty ds WHERE ds.specialty.id = :specialtyId")
    List<DoctorSpecialty> findAllBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    @Query("SELECT COUNT(ds) FROM DoctorSpecialty ds WHERE ds.specialty.id = :specialtyId")
    long countBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    @Query("SELECT COUNT(ds) FROM DoctorSpecialty ds WHERE ds.doctor.id = :doctorId")
    long countByDoctorId(@Param("doctorId") Long doctorId);
    
    void deleteByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);
    
    void deleteByDoctorId(Long doctorId);
    
    void deleteBySpecialtyId(Long specialtyId);
}