package com.hospital.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hospital Management System API")
                        .description("""
                                **Sistema Integral de Gestión Hospitalaria**
                                
                                API REST moderna para la gestión completa de operaciones hospitalarias, 
                                incluyendo autenticación, gestión de usuarios, citas médicas, historiales 
                                clínicos, pagos y analítica avanzada.
                                
                                **🔐 Autenticación:** Para probar los endpoints, autentíquese primero en el grupo 
                                "Autenticación" usando `POST /auth/login` y luego use el botón "Authorize" 
                                con el token JWT obtenido.
                                """)
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Hospital API Team")
                                .email("dev@hospital.pe")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desarrollo")))
                .components(new Components()
                        .addSecuritySchemes("JWT Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT. Formato: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("JWT Authentication"));
    }

    // CONFIGURACIÓN DEFINITIVA PARA GRUPOS QUE SÍ FUNCIONA
    
    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .displayName("🔐 Autenticación")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("users")
                .displayName("👥 Usuarios")
                .pathsToMatch("/patients/**", "/doctors/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appointmentsApi() {
        return GroupedOpenApi.builder()
                .group("appointments")
                .displayName("📅 Citas")
                .pathsToMatch("/appointments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi catalogsApi() {
        return GroupedOpenApi.builder()
                .group("catalogs")
                .displayName("📋 Catálogos")
                .pathsToMatch("/specialties/**", "/document-types/**", "/payment-methods/**")
                .build();
    }

    @Bean
    public GroupedOpenApi medicalApi() {
        return GroupedOpenApi.builder()
                .group("medical")
                .displayName("🏥 Médico")
                .pathsToMatch("/medical-records/**", "/prescriptions/**", "/medical-attachments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("payments")
                .displayName("💰 Pagos")
                .pathsToMatch("/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationsApi() {
        return GroupedOpenApi.builder()
                .group("notifications")
                .displayName("🔔 Notificaciones")
                .pathsToMatch("/notifications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatbotApi() {
        return GroupedOpenApi.builder()
                .group("chatbot")
                .displayName("🤖 Chatbot")
                .pathsToMatch("/chatbot/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("⚙️ Administración")
                .pathsToMatch("/admin/**", "/analytics/**", "/audit/**")
                .build();
    }
}
