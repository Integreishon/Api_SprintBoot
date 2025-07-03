package com.hospital.backend.external.service;

import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.external.dto.ReniecResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReniecService {

    private final WebClient webClient;

    @Value("${api.reniec.url}")
    private String reniecApiUrl;

    @Value("${api.reniec.token}")
    private String reniecApiToken;

    public ReniecService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(reniecApiUrl).build();
    }

    public Mono<ReniecResponse> getDniData(String dni) {
        log.info("Consultando DNI {} en la API de RENIEC", dni);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/reniec/dni").queryParam("numero", dni).build())
                .header("Authorization", reniecApiToken)
                .retrieve()
                .onStatus(
                    httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Error desde la API de RENIEC: {} - {}", clientResponse.statusCode(), errorBody);
                            return Mono.error(new BusinessException("Error al consultar el DNI: " + errorBody));
                        })
                )
                .bodyToMono(ReniecResponse.class)
                .doOnSuccess(response -> log.info("Respuesta exitosa de RENIEC para DNI {}", dni))
                .doOnError(error -> log.error("Fallo la consulta a RENIEC para DNI {}", dni, error));
    }
} 