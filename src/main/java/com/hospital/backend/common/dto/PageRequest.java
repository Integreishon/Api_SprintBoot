package com.hospital.backend.common.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    //CREOOO QUE NO VA ESTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    @Min(value = 0, message = "El número de página debe ser mayor o igual a 0")
    private int page = 0;
    
    @Min(value = 1, message = "El tamaño de página debe ser mayor a 0")
    private int size = 10;
    
    private String sortBy = "id";
    private String sortDirection = "asc";
    
    public org.springframework.data.domain.PageRequest toPageable() {
        org.springframework.data.domain.Sort.Direction direction = 
            "desc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC;
        
        return org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
    }
}