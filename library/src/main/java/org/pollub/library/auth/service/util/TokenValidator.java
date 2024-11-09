package org.pollub.library.auth.service.util;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenValidator {
    private final TokenClaimsExtractor claimsExtractor;

    public boolean isValid(String token, User user) {
        String username = claimsExtractor.extractClaim(token, Claims::getSubject);
        return username.equals(user.getUsername()) && !isExpired(token);
    }

    public boolean isExpired(String token) {
        Date expiration = claimsExtractor.extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}