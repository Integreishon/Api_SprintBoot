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

    // ORDEN CONTROLADO: "Todas las APIs" primero con prefijo 01
    @Bean
    public GroupedOpenApi allApisApi() {
        return GroupedOpenApi.builder()
                .group("01-all")
                .displayName("🌐 Todas las APIs")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Hospital Management System API")
                            .description("""
                                    **Sistema de Gestión Hospitalaria**
                                    
                                    API REST para operaciones hospitalarias completas.
                                    
                                    **🔑 Autenticación:** Use `/auth/login` → Botón "Authorize" → `Bearer {token}`
                                    """)
                            .version("2.0.0")
                            .contact(new Contact()
                                    .name("Hospital API Team")
                                    .email("dev@hospital.pe")));
                })
                .build();
    }

    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("02-authentication")
                .displayName("🔐 Autenticación y Sesiones")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("03-users")
                .displayName("👥 Pacientes y Doctores")
                .pathsToMatch("/patients/**", "/doctors/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appointmentsApi() {
        return GroupedOpenApi.builder()
                .group("04-appointments")
                .displayName("📅 Citas Médicas")
                .pathsToMatch("/appointments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi catalogsApi() {
        return GroupedOpenApi.builder()
                .group("05-catalogs")
                .displayName("📋 Referencias")
                .pathsToMatch("/specialties/**", "/document-types/**", "/payment-methods/**")
                .build();
    }

    @Bean
    public GroupedOpenApi medicalApi() {
        return GroupedOpenApi.builder()
                .group("06-medical")
                .displayName("🏥 Historiales Médicos")
                .pathsToMatch("/medical-records/**", "/prescriptions/**", "/medical-attachments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("07-payments")
                .displayName("💰 Facturación y Pagos")
                .pathsToMatch("/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationsApi() {
        return GroupedOpenApi.builder()
                .group("08-notifications")
                .displayName("🔔 Notificaciones")
                .pathsToMatch("/notifications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi chatbotApi() {
        return GroupedOpenApi.builder()
                .group("09-chatbot")
                .displayName("🤖 Asistente Virtual")
                .pathsToMatch("/chatbot/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("10-admin")
                .displayName("⚙️ Panel de Administración")
                .pathsToMatch("/admin/**", "/analytics/**", "/audit/**")
                .build();
    }
}
