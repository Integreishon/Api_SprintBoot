// Proveedor de tokens JWT para generar y validar tokens
package com.hospital.backend.security;

import com.hospital.backend.auth.entity.User;
import com.hospital.backend.common.exception.UnauthorizedException;
import com.hospital.backend.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final JwtProperties jwtProperties;
    private SecretKey key;
    
    private SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }
    
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationTime());
        
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer(jwtProperties.getIssuer())
                .signWith(getKey())
                .compact();
    }
    
    public Claims validateToken(String token) {
        try {
            // Validar que el token no esté vacío o sea null
            if (token == null || token.trim().isEmpty()) {
                throw new UnauthorizedException("Token vacío o nulo");
            }
            
            // Parsear y validar el token
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // Validaciones adicionales
            validateTokenClaims(claims);
            
            log.debug("Token JWT validado exitosamente para usuario: {}", claims.getSubject());
            return claims;
            
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
            throw new UnauthorizedException("Token expirado");
            
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.warn("Token JWT no soportado: {}", e.getMessage());
            throw new UnauthorizedException("Formato de token no soportado");
            
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("Token JWT malformado: {}", e.getMessage());
            throw new UnauthorizedException("Token malformado");
            
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("Firma de token JWT inválida: {}", e.getMessage());
            throw new UnauthorizedException("Firma de token inválida");
            
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error general en token JWT: {}", e.getMessage());
            throw new UnauthorizedException("Token inválido");
        }
    }
    
    /**
     * Valida los claims del token JWT
     */
    private void validateTokenClaims(Claims claims) {
        // Validar que tenga subject (userId)
        if (claims.getSubject() == null || claims.getSubject().trim().isEmpty()) {
            throw new UnauthorizedException("Token sin identificador de usuario");
        }
        
        // Validar que el subject sea un número válido
        try {
            Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Identificador de usuario inválido en token");
        }
        
        // Validar que tenga issuer correcto
        if (!jwtProperties.getIssuer().equals(claims.getIssuer())) {
            throw new UnauthorizedException("Emisor de token inválido");
        }
        
        // Validar que tenga email
        String email = claims.get("email", String.class);
        if (email == null || email.trim().isEmpty()) {
            throw new UnauthorizedException("Token sin email de usuario");
        }
        
        // Validar que tenga rol
        String role = claims.get("role", String.class);
        if (role == null || role.trim().isEmpty()) {
            throw new UnauthorizedException("Token sin rol de usuario");
        }
    }
    
    public long getExpirationTime() {
        return jwtProperties.getExpirationTime();
    }
    
    /**
     * Verifica si un token está expirado sin lanzar excepción
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // Si hay cualquier error, consideramos el token como expirado
        }
    }
    
    /**
     * Extrae el email del token sin validar completamente
     */
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}