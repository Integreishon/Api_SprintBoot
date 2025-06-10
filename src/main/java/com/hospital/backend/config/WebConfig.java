// Configuración Web MVC para API REST y manejo de archivos
package com.hospital.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos médicos estáticos (exámenes, documentos)
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/medical-files/")
                .setCachePeriod(3600);
                
        // Servir archivos de documentación
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/static/docs/");
    }
}