// Repository para gestión de pacientes
package com.hospital.backend.user.repository;

import com.hospital.backend.user.entity.Patient;
import com.hospital.backend.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository para gestión de pacientes
 * Adaptado a la nueva lógica de Urovital
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByUserId(Long userId);
    
    Optional<Patient> findByUserEmail(String email);
    
    Optional<Patient> findByUser(com.hospital.backend.auth.entity.User user);
    
    boolean existsByUserEmail(String email);
    
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.user")
    Page<Patient> findAllWithUser(Pageable pageable);
    
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.user WHERE p.id = :id")
    Optional<Patient> findByIdWithUser(@Param("id") Long id);
    
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Patient> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    Page<Patient> findByGender(Gender gender, Pageable pageable);
    
    @Query("SELECT p FROM Patient p WHERE p.birthDate BETWEEN :startDate AND :endDate")
    Page<Patient> findByBirthDateBetween(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate, 
                                       Pageable pageable);
    
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "(:name IS NULL OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:dni IS NULL OR u.dni LIKE %:dni%) AND " +
           "(:gender IS NULL OR p.gender = :gender)")
    Page<Patient> findByFilters(@Param("name") String name,
                              @Param("dni") String dni,
                              @Param("gender") Gender gender,
                              Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE YEAR(p.birthDate) = :birthYear")
    long countByBirthYear(@Param("birthYear") int birthYear);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender")
    long countByGender(@Param("gender") Gender gender);
    
    @Query("SELECT p FROM Patient p WHERE p.phone = :phone")
    Optional<Patient> findByPhone(@Param("phone") String phone);
}