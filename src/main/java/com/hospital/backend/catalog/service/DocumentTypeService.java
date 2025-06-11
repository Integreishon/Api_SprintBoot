package com.hospital.backend.catalog.service;

import com.hospital.backend.catalog.dto.request.DocumentTypeRequest;
import com.hospital.backend.catalog.dto.response.DocumentTypeResponse;
import com.hospital.backend.catalog.entity.DocumentType;
import com.hospital.backend.catalog.repository.DocumentTypeRepository;
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
 * Servicio para la gestión de tipos de documento en el sistema hospitalario
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentTypeService {
    
    private final DocumentTypeRepository documentTypeRepository;
    
    @Transactional(readOnly = true)
    public DocumentTypeResponse findById(Long id) {
        DocumentType documentType = getDocumentTypeById(id);
        return mapToDocumentTypeResponse(documentType);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<DocumentTypeResponse> findAll(Pageable pageable) {
        Page<DocumentType> documentTypes = documentTypeRepository.findAll(pageable);
        Page<DocumentTypeResponse> documentTypeResponses = documentTypes.map(this::mapToDocumentTypeResponse);
        return new PageResponse<>(documentTypeResponses);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<DocumentTypeResponse> findByActiveStatus(Boolean isActive, Pageable pageable) {
        Page<DocumentType> documentTypes = documentTypeRepository.findByIsActive(isActive, pageable);
        Page<DocumentTypeResponse> documentTypeResponses = documentTypes.map(this::mapToDocumentTypeResponse);
        return new PageResponse<>(documentTypeResponses);
    }
    
    @Transactional(readOnly = true)
    public List<DocumentTypeResponse> findAllActive() {
        return documentTypeRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDocumentTypeResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public DocumentTypeResponse findByCode(String code) {
        return documentTypeRepository.findByCode(code)
                .map(this::mapToDocumentTypeResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento", "código", code));
    }
    
    public DocumentTypeResponse create(DocumentTypeRequest request) {
        // Validar que no exista un tipo de documento con el mismo código
        if (documentTypeRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Ya existe un tipo de documento con el código: " + request.getCode());
        }
        
        // Crear el nuevo tipo de documento
        DocumentType documentType = new DocumentType();
        documentType.setCode(request.getCode());
        documentType.setName(request.getName());
        documentType.setValidationPattern(request.getValidationPattern());
        documentType.setIsActive(request.getIsActive());
        
        DocumentType savedDocumentType = documentTypeRepository.save(documentType);
        log.info("Tipo de documento creado con ID: {} y código: {}", savedDocumentType.getId(), savedDocumentType.getCode());
        
        return mapToDocumentTypeResponse(savedDocumentType);
    }
    
    public DocumentTypeResponse update(Long id, DocumentTypeRequest request) {
        DocumentType documentType = getDocumentTypeById(id);
        
        // Validar que no exista otro documento con el mismo código (diferente al actual)
        if (!documentType.getCode().equals(request.getCode()) && documentTypeRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Ya existe un tipo de documento con el código: " + request.getCode());
        }
        
        // Actualizar el tipo de documento
        documentType.setCode(request.getCode());
        documentType.setName(request.getName());
        documentType.setValidationPattern(request.getValidationPattern());
        documentType.setIsActive(request.getIsActive());
        
        DocumentType updatedDocumentType = documentTypeRepository.save(documentType);
        log.info("Tipo de documento actualizado con ID: {} y código: {}", updatedDocumentType.getId(), updatedDocumentType.getCode());
        
        return mapToDocumentTypeResponse(updatedDocumentType);
    }
    
    public void delete(Long id) {
        DocumentType documentType = getDocumentTypeById(id);
        
        // En lugar de eliminar físicamente, marcamos como inactivo
        documentType.setIsActive(false);
        documentTypeRepository.save(documentType);
        log.info("Tipo de documento marcado como inactivo, ID: {}", id);
    }
    
    // Método de utilidad para obtener un tipo de documento por ID
    private DocumentType getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento", "id", id));
    }
    
    // Mapear entidad a DTO de respuesta
    private DocumentTypeResponse mapToDocumentTypeResponse(DocumentType documentType) {
        DocumentTypeResponse response = new DocumentTypeResponse();
        response.setId(documentType.getId());
        response.setCode(documentType.getCode());
        response.setName(documentType.getName());
        response.setValidationPattern(documentType.getValidationPattern());
        response.setIsActive(documentType.getIsActive());
        return response;
    }
}
