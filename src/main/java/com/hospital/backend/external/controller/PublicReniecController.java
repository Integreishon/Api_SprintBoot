package com.hospital.backend.external.controller;

import com.hospital.backend.external.dto.ReniecResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicReniecController {

    private final RestTemplate restTemplate;
    
    @Value("${api.reniec.url}")
    private String reniecApiUrl;

    @Value("${api.reniec.token}")
    private String reniecApiToken;

    public PublicReniecController() {
        this.restTemplate = new RestTemplate();
    }

    @CrossOrigin // Permitir CORS para este endpoint
    @GetMapping("/reniec/dni/{dni}")
    public ResponseEntity<?> getDniInfo(@PathVariable String dni) {
        log.info("Consultando DNI público: {}", dni);
        
        try {
            // Crear headers con el token de autorización
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", reniecApiToken);
            
            // Construir la URL completa para la API de RENIEC
            String url = reniecApiUrl + "/reniec/dni?numero=" + dni;
            
            log.info("Llamando a URL externa: {}", url);
            
            // Crear la entidad HTTP con los headers
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Realizar la petición GET a la API externa
            ResponseEntity<ReniecResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                ReniecResponse.class
            );
            
            log.info("Respuesta de RENIEC recibida: {}", response.getStatusCode());
            
            // Retornar la respuesta directamente
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Error al consultar el DNI: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReniecResponse()); // Devolver objeto vacío en caso de error
        }
    }
} 