package com.hospital.backend.catalog.repository;

import com.hospital.backend.catalog.entity.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    
    Optional<DocumentType> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<DocumentType> findByIsActiveTrue();
    
    Page<DocumentType> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<DocumentType> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
