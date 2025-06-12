// Configuración Web MVC para API REST - SIMPLIFICADA
package com.hospital.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos (incluye nuestro CSS personalizado)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
