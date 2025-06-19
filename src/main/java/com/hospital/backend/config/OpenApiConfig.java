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
    OpenAPI customOpenAPI() {
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
    GroupedOpenApi allApisApi() {
        return GroupedOpenApi.builder()
                .group("01-all")
                .displayName("🌐 Todas las APIs")
                .pathsToMatch("/**")
                .pathsToExclude("/actuator/**") // Excluir endpoints internos de Actuator
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Hospital Management System API")
                            .description("""
                                    **Sistema de Gestión Hospitalaria**
                                    
                                    API REST para operaciones hospitalarias completas.
                                    
                                    **🔑 Autenticación:** Use `/auth/login` → Botón "Authorize" → `Bearer {token}`
                                    
                                    **📊 Monitoreo:** Grupo "Monitoreo y Métricas" para health checks y métricas del sistema
                                    """)
                            .version("2.0.0")
                            .contact(new Contact()
                                    .name("Hospital API Team")
                                    .email("dev@hospital.pe")));
                })
                .build();
    }

    @Bean
    GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("02-authentication")
                .displayName("🔐 Autenticación y Sesiones")
                .pathsToMatch("/auth/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Sistema de Autenticación JWT")
                            .description("""
                                    **Autenticación Segura con JWT**
                                    
                                    Sistema completo de autenticación con tokens JWT:
                                    - Login y registro de usuarios
                                    - Validación y renovación de tokens
                                    - Gestión de perfiles y contraseñas
                                    
                                    🔑 **Para obtener token:** Use POST /auth/login
                                    """));
                })
                .build();
    }

    @Bean
    GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("03-users")
                .displayName("👥 Pacientes y Doctores")
                .pathsToMatch("/patients/**", "/doctors/**")
                .build();
    }

    @Bean
    GroupedOpenApi appointmentsApi() {
        return GroupedOpenApi.builder()
                .group("04-appointments")
                .displayName("📅 Citas Médicas")
                .pathsToMatch("/appointments/**")
                .build();
    }

    @Bean
    GroupedOpenApi catalogsApi() {
        return GroupedOpenApi.builder()
                .group("05-catalogs")
                .displayName("📋 Catálogos y Referencias")
                .pathsToMatch("/specialties/**", "/document-types/**", "/payment-methods/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Catálogos del Sistema")
                            .description("""
                                    **Referencias y Configuraciones**
                                    
                                    Catálogos básicos del sistema:
                                    - 🏥 Especialidades médicas con precios
                                    - 🆔 Tipos de documento de identidad
                                    - 💳 Métodos de pago disponibles
                                    
                                    🌐 **Acceso público** - No requieren autenticación
                                    """));
                })
                .build();
    }

    @Bean
    GroupedOpenApi medicalApi() {
        return GroupedOpenApi.builder()
                .group("06-medical")
                .displayName("🏥 Historiales Médicos")
                .pathsToMatch("/medical-records/**", "/prescriptions/**", "/medical-attachments/**")
                .build();
    }

    @Bean
    GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("07-payments")
                .displayName("💰 Facturación y Pagos")
                .pathsToMatch("/payments/**")
                .build();
    }

    @Bean
    GroupedOpenApi notificationsApi() {
        return GroupedOpenApi.builder()
                .group("08-notifications")
                .displayName("🔔 Notificaciones y Alertas")
                .pathsToMatch("/notifications/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Sistema de Notificaciones")
                            .description("""
                                    **Comunicaciones del Hospital**
                                    
                                    Tipos de notificaciones:
                                    - 📧 Recordatorios de citas por email
                                    - 🔔 Alertas del sistema
                                    - 📨 Notificaciones masivas
                                    - 📈 Estadísticas de entrega
                                    """));
                })
                .build();
    }

    @Bean
    GroupedOpenApi chatbotApi() {
        return GroupedOpenApi.builder()
                .group("09-chatbot")
                .displayName("🤖 Asistente Virtual")
                .pathsToMatch("/chatbot/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Chatbot Inteligente")
                            .description("""
                                    **Asistente Virtual del Hospital**
                                    
                                    Capacidades del chatbot:
                                    - ❓ Respuestas automáticas a consultas
                                    - 📚 Base de conocimientos configurable
                                    - 📊 Historial de conversaciones
                                    - 👍 Sistema de feedback
                                    
                                    🌐 **Acceso público** para consultas básicas
                                    """));
                })
                .build();
    }

    @Bean
    GroupedOpenApi monitoringApi() {
        return GroupedOpenApi.builder()
                .group("10-monitoring")
                .displayName("📊 Monitoreo y Métricas")
                .pathsToMatch("/monitoring/**")
                .build();
    }

    @Bean
    GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("11-admin")
                .displayName("⚙️ Panel de Administración")
                .pathsToMatch("/admin/**", "/analytics/**", "/audit/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("Administración del Hospital")
                            .description("""
                                    **Panel de Control Administrativo**
                                    
                                    Herramientas de administración:
                                    - 📈 Dashboard con métricas en tiempo real
                                    - 📄 Auditoría de acciones del sistema
                                    - 📊 Análisis y reportes avanzados
                                    - ⚙️ Configuraciones del hospital
                                    
                                    🔒 **Solo ADMIN** - Requiere permisos de administrador
                                    """));
                })
                .build();
    }
}
