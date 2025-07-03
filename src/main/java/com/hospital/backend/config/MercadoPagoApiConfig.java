package com.hospital.backend.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MercadoPagoApiConfig {

    @Value("${mercadopago.access-token}")
    private String accessToken;
    
    @Value("${mercadopago.public-key}")
    private String publicKey;

    @PostConstruct
    public void init() {
        try {
            log.info("Inicializando configuración de Mercado Pago");
            log.info("Access Token: {}", accessToken.substring(0, 10) + "...");
            
            MercadoPagoConfig.setAccessToken(accessToken);
            
            log.info("Configuración de Mercado Pago inicializada correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar la configuración de Mercado Pago", e);
            throw new RuntimeException("Error al inicializar Mercado Pago: " + e.getMessage());
        }
    }
} 