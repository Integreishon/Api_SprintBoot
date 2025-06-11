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
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtProperties.getIssuer())
                .signWith(getKey())
                .compact();
    }
    
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
            throw new UnauthorizedException("Token inválido");
        }
    }
    
    public long getExpirationTime() {
        return jwtProperties.getExpirationTime();
    }
}