package com.hospital.backend.catalog.controller;

import com.hospital.backend.catalog.dto.request.DocumentTypeRequest;
import com.hospital.backend.catalog.dto.response.DocumentTypeResponse;
import com.hospital.backend.catalog.service.DocumentTypeService;
import com.hospital.backend.common.dto.ApiResponse;
import com.hospital.backend.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de tipos de documentos
 */
@RestController
@RequestMapping("/document-types")
@RequiredArgsConstructor
@Tag(name = "📝 Tipos de Documento", description = "Catálogo de tipos de documentos de identidad con validaciones.")
public class DocumentTypeController {
    
    private final DocumentTypeService documentTypeService;
    
    @GetMapping
    @Operation(summary = "Listar todos los tipos de documento", 
               description = "Obtiene un listado paginado de tipos de documento")
    public ResponseEntity<PageResponse<DocumentTypeResponse>> getAllDocumentTypes(
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        
        if (active != null) {
            return ResponseEntity.ok(documentTypeService.findByActiveStatus(active, pageable));
        }
        
        return ResponseEntity.ok(documentTypeService.findAll(pageable));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Listar tipos de documento activos", 
               description = "Obtiene un listado de todos los tipos de documento activos (no paginado)")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponse>>> getAllActiveDocumentTypes() {
        List<DocumentTypeResponse> documentTypes = documentTypeService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success("Tipos de documento activos recuperados", documentTypes));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de documento por ID", 
               description = "Recupera un tipo de documento específico por su ID")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> getDocumentTypeById(@PathVariable Long id) {
        DocumentTypeResponse documentType = documentTypeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Tipo de documento encontrado", documentType));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Obtener tipo de documento por código", 
               description = "Recupera un tipo de documento específico por su código")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> getDocumentTypeByCode(@PathVariable String code) {
        DocumentTypeResponse documentType = documentTypeService.findByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Tipo de documento encontrado", documentType));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear tipo de documento", 
               description = "Registra un nuevo tipo de documento en el sistema")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> createDocumentType(
            @Valid @RequestBody DocumentTypeRequest request) {
        
        DocumentTypeResponse createdDocumentType = documentTypeService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tipo de documento creado exitosamente", createdDocumentType));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar tipo de documento", 
               description = "Actualiza un tipo de documento existente")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> updateDocumentType(
            @PathVariable Long id, 
            @Valid @RequestBody DocumentTypeRequest request) {
        
        DocumentTypeResponse updatedDocumentType = documentTypeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tipo de documento actualizado exitosamente", updatedDocumentType));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar tipo de documento", 
               description = "Elimina un tipo de documento del sistema (marcándolo como inactivo)")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentType(@PathVariable Long id) {
        documentTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Tipo de documento eliminado exitosamente", null));
    }
}
