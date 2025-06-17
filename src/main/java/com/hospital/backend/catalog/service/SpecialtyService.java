package com.hospital.backend.catalog.service;

import com.hospital.backend.catalog.dto.request.SpecialtyRequest;
import com.hospital.backend.catalog.dto.response.SpecialtyResponse;
import com.hospital.backend.catalog.entity.Specialty;
import com.hospital.backend.catalog.repository.SpecialtyRepository;
import com.hospital.backend.common.dto.PageResponse;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de especialidades médicas en el sistema hospitalario
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    
    @Transactional(readOnly = true)
    public SpecialtyResponse findById(Long id) {
        Specialty specialty = getSpecialtyById(id);
        return mapToSpecialtyResponse(specialty);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<SpecialtyResponse> findAll(Pageable pageable) {
        Page<Specialty> specialties = specialtyRepository.findAll(pageable);
        Page<SpecialtyResponse> specialtyResponses = specialties.map(this::mapToSpecialtyResponse);
        return new PageResponse<>(specialtyResponses);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<SpecialtyResponse> findByActiveStatus(Boolean isActive, Pageable pageable) {
        Page<Specialty> specialties = specialtyRepository.findByIsActive(isActive, pageable);
        Page<SpecialtyResponse> specialtyResponses = specialties.map(this::mapToSpecialtyResponse);
        return new PageResponse<>(specialtyResponses);
    }
    
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> findAllActive() {
        return specialtyRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToSpecialtyResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PageResponse<SpecialtyResponse> searchByName(String name, Pageable pageable) {
        Page<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase(name, pageable);
        Page<SpecialtyResponse> specialtyResponses = specialties.map(this::mapToSpecialtyResponse);
        return new PageResponse<>(specialtyResponses);
    }
    
    public SpecialtyResponse create(SpecialtyRequest request) {
        // Validar que no exista una especialidad con el mismo nombre
        if (specialtyRepository.existsByName(request.getName())) {
            throw new BusinessException("Ya existe una especialidad con el nombre: " + request.getName());
        }
        
        // Crear la nueva especialidad
        Specialty specialty = new Specialty();
        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        specialty.setConsultationPrice(request.getConsultationPrice());
        specialty.setDiscountPercentage(request.getDiscountPercentage());
        specialty.setIsActive(request.getIsActive());
        
        Specialty savedSpecialty = specialtyRepository.save(specialty);
        log.info("Especialidad creada con ID: {} y nombre: {}", savedSpecialty.getId(), savedSpecialty.getName());
        
        return mapToSpecialtyResponse(savedSpecialty);
    }
    
    public SpecialtyResponse update(Long id, SpecialtyRequest request) {
        Specialty specialty = getSpecialtyById(id);
        
        // Validar que no exista otra especialidad con el mismo nombre (diferente a la actual)
        if (!specialty.getName().equals(request.getName()) && 
                specialtyRepository.existsByName(request.getName())) {
            throw new BusinessException("Ya existe una especialidad con el nombre: " + request.getName());
        }
        
        // Actualizar la especialidad
        specialty.setName(request.getName());
        specialty.setDescription(request.getDescription());
        specialty.setConsultationPrice(request.getConsultationPrice());
        specialty.setDiscountPercentage(request.getDiscountPercentage());
        specialty.setIsActive(request.getIsActive());
        
        Specialty updatedSpecialty = specialtyRepository.save(specialty);
        log.info("Especialidad actualizada con ID: {}", updatedSpecialty.getId());
        
        return mapToSpecialtyResponse(updatedSpecialty);
    }
    
    public void delete(Long id) {
        Specialty specialty = getSpecialtyById(id);
        
        // En lugar de eliminar físicamente, marcamos como inactiva
        specialty.setIsActive(false);
        specialtyRepository.save(specialty);
        log.info("Especialidad marcada como inactiva, ID: {}", id);
    }
    
    // Método de utilidad para obtener una especialidad por ID
    private Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", id));
    }
    
    // Mapear entidad a DTO de respuesta
    private SpecialtyResponse mapToSpecialtyResponse(Specialty specialty) {
        SpecialtyResponse response = new SpecialtyResponse();
        response.setId(specialty.getId());
        response.setName(specialty.getName());
        response.setDescription(specialty.getDescription());
        response.setConsultationPrice(specialty.getConsultationPrice());
        response.setDiscountPercentage(specialty.getDiscountPercentage());
        response.setFinalPrice(specialty.getFinalPrice());
        response.setIsActive(specialty.getIsActive());
        return response;
    }
}
