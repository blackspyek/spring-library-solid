package org.pollub.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Shared JWT Token Provider for local validation in all services.
 * Each service uses this to validate tokens without calling auth-service.
 */
public class JwtTokenProvider {
    
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    private final SecretKey signingKey;
    
    public JwtTokenProvider(String jwtSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public List<String> getRolesFromToken(String token) {
        Object rolesObj = parseToken(token).get("role");

        if (rolesObj instanceof String role) {
            return List.of(role);
        } else if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }
    
    public boolean validateToken(String token) {
        try {
            validateTokenAndThrow(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

    public void validateTokenAndThrow(String token) throws JwtException, IllegalArgumentException {
        Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }
    
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
