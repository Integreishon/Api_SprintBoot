// Respuesta para datos paginados con metadatos de paginación
package com.hospital.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response DTO que encapsula respuestas paginadas para las APIs
 * @param <T> tipo de dato contenido en la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean hasNext;
    private boolean hasPrevious;
    
    /**
     * Constructor a partir de un objeto Page de Spring
     * @param page objeto Page de Spring
     */
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}