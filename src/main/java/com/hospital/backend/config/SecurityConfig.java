// Configuración de Spring Security con JWT
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Autenticación pública - CON Y SIN /api prefix
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                
                // Catálogos públicos - CON Y SIN /api prefix
                .requestMatchers("/document-types/**").permitAll()
                .requestMatchers("/api/document-types/**").permitAll()
                .requestMatchers("/specialties/**").permitAll()
                .requestMatchers("/api/specialties/**").permitAll()
                .requestMatchers("/payment-methods/**").permitAll()
                .requestMatchers("/api/payment-methods/**").permitAll()
                
                // Chatbot público - CON Y SIN /api prefix
                .requestMatchers("/chatbot/**").permitAll()
                .requestMatchers("/api/chatbot/**").permitAll()
                
                // Swagger UI y documentación - CON Y SIN /api prefix - COMPLETO Y DETALLADO
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/swagger-ui/**", "/api/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers("/api/v3/api-docs/**", "/api/v3/api-docs").permitAll()
                .requestMatchers("/v3/api-docs/swagger-config").permitAll() // RUTA CRÍTICA
                .requestMatchers("/api/v3/api-docs/swagger-config").permitAll() // RUTA CRÍTICA
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/swagger-check").permitAll()
                .requestMatchers("/api/swagger-check").permitAll()
                
                // Actuator - CON Y SIN /api prefix
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/actuator/**").permitAll()
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}