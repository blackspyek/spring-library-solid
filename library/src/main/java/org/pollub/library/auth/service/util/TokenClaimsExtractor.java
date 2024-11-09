package org.pollub.library.auth.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.TokenProcessingException;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class TokenClaimsExtractor {
    private final TokenConfig tokenConfig;

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new TokenProcessingException("Token has expired", e);
        } catch (Exception e) {
            throw new TokenProcessingException("Failed to extract claims from token", e);
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(tokenConfig.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
