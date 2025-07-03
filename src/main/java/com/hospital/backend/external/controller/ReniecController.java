package com.hospital.backend.external.controller;

import com.hospital.backend.external.dto.ReniecResponse;
import com.hospital.backend.external.service.ReniecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/external")
@RequiredArgsConstructor
@Tag(name = "External APIs", description = "Endpoints for communication with external services")
public class ReniecController {

    private final ReniecService reniecService;

    @GetMapping("/reniec/dni/{dni}")
    @Operation(summary = "Get DNI information from RENIEC", description = "Fetches person data from the external RENIEC service using a DNI number.")
    public Mono<ReniecResponse> getDniInfo(@PathVariable String dni) {
        return reniecService.getDniData(dni);
    }
} 