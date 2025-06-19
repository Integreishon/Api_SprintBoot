// Filtro de autenticación JWT para validar tokens en cada request
package com.hospital.backend.security;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.auth.repository.UserRepository;
import com.hospital.backend.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                // Validar token JWT - lanza excepción si es inválido
                Claims claims = tokenProvider.validateToken(jwt);
                Long userId = Long.parseLong(claims.getSubject());
                
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
                
                if (!user.getIsActive()) {
                    throw new UnauthorizedException("Usuario inactivo");
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuario autenticado: {} con rol: {}", user.getEmail(), user.getRole());
            }
            
            // Continuar con el filtro solo si no hay excepciones
            filterChain.doFilter(request, response);
            
        } catch (UnauthorizedException ex) {
            // Manejar tokens inválidos o usuarios no autorizados
            log.warn("Token JWT inválido o usuario no autorizado: {}", ex.getMessage());
            handleUnauthorized(response, ex.getMessage());
            return; // NO continuar con el filtro
            
        } catch (JwtException ex) {
            // Manejar errores específicos de JWT (malformado, expirado, etc.)
            log.warn("Error en token JWT: {}", ex.getMessage());
            handleUnauthorized(response, "Token JWT inválido");
            return; // NO continuar con el filtro
            
        } catch (NumberFormatException ex) {
            // Manejar error al parsear userId del token
            log.warn("Token JWT contiene userId inválido: {}", ex.getMessage());
            handleUnauthorized(response, "Token JWT malformado");
            return; // NO continuar con el filtro
            
        } catch (Exception ex) {
            // Manejar cualquier otro error inesperado
            log.error("Error inesperado en autenticación JWT: ", ex);
            handleInternalError(response);
            return; // NO continuar con el filtro
        }
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
    
    /**
     * Maneja respuestas de error 401 Unauthorized
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"UNAUTHORIZED\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
            message,
            java.time.Instant.now().toString()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
    
    /**
     * Maneja respuestas de error 500 Internal Server Error
     */
    private void handleInternalError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"INTERNAL_ERROR\",\"message\":\"Error interno de autenticación\",\"timestamp\":\"%s\"}",
            java.time.Instant.now().toString()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}