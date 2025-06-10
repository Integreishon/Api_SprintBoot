// Configuración de JPA/Hibernate para PostgreSQL
package com.hospital.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.hospital.backend")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    
}