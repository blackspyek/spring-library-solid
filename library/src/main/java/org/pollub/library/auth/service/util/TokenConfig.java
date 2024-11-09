package org.pollub.library.auth.service.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.pollub.library.exception.TokenConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Getter
public class TokenConfig {
    private final SecretKey signingKey;
    private final long expirationTime;

    public TokenConfig(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expiration-time}") long expirationTime
    ) {
        this.signingKey = initializeSigningKey(secretKey);
        this.expirationTime = expirationTime;
    }

    private SecretKey initializeSigningKey(String secretKey) {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (Exception e) {
            throw new TokenConfigurationException("Failed to initialize signing key", e);
        }
    }
}
