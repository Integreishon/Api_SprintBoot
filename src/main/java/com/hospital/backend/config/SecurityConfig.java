// Configuración de Spring Security con JWT - VERSIÓN LIMPIA
package com.hospital.backend.config;

import com.hospital.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Autenticación pública
                .requestMatchers("/auth/**").permitAll()
                
                // Registro público de pacientes
                .requestMatchers("/patients/register").permitAll()
                
                // Catálogos públicos
                .requestMatchers("/document-types/**").permitAll()
                .requestMatchers("/specialties/**").permitAll()
                .requestMatchers("/doctors/specialty/**").permitAll()
                .requestMatchers("/payment-methods/**").permitAll()
                
                // Chatbot público
                .requestMatchers("/chatbot/**").permitAll()
                
                // Portal Virtual - Rutas específicas
                .requestMatchers("/appointments/virtual").hasRole("PATIENT")
                .requestMatchers("/appointments/me").hasRole("PATIENT")
                .requestMatchers("/appointments/*/receipt").hasRole("PATIENT")
                .requestMatchers("/reception/**").hasRole("RECEPTIONIST")
                
                // Swagger UI y documentación
                .requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**", "/v3/api-docs/swagger-config",
                    "/swagger-resources/**", "/webjars/**"
                ).permitAll()
                
                // Actuator para monitoreo
                .requestMatchers("/actuator/**").permitAll()
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
