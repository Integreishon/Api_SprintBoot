package com.hospital.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏥 Hospital Management API")
                        .description("Sistema Integral de Gestión Hospitalaria - API REST completa para la gestión de citas, pacientes, doctores, historiales médicos y administración hospitalaria.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hospital API Development Team")
                                .email("dev@hospital.pe")
                                .url("https://github.com/hospital-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese su token JWT en el formato: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

    // ✅ PASO 1: AUTENTICACIÓN (FUNCIONANDO)
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    // ✅ PASO 2: ACTIVANDO APPOINTMENTS AHORA
    @Bean
    public GroupedOpenApi appointmentApi() {
        return GroupedOpenApi.builder()
                .group("appointments")
                .pathsToMatch("/api/v1/appointments/**")
                .build();
    }

    /*
    // ⏳ PASO 3: Si appointments funciona, probar medical
    @Bean
    public GroupedOpenApi medicalApi() {
        return GroupedOpenApi.builder()
                .group("medical")
                .pathsToMatch("/api/medical-records/**", "/api/prescriptions/**")
                .build();
    }
    */

    /*
    // ⏳ PASO 4: Si medical funciona, probar admin
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("administration")
                .pathsToMatch("/api/dashboard/**", "/api/analytics/**", "/api/settings/**")
                .build();
    }
    */

    /*
    // ⏳ PASO 5: Si admin funciona, probar hospital-api general
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("hospital-api")
                .pathsToMatch("/api/**")
                .packagesToScan("com.hospital.backend")
                .build();
    }
    */
}