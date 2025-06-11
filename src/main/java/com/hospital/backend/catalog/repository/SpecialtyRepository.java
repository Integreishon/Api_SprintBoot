package com.hospital.backend.catalog.repository;

import com.hospital.backend.catalog.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    
    Page<Specialty> findByIsActive(Boolean isActive, Pageable pageable);
    
    List<Specialty> findByIsActiveTrue();
    
    Page<Specialty> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    boolean existsByName(String name);
}
