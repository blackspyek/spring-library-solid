package org.pollub.library.auth.service.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.service.util.TokenClaimsExtractor;
import org.pollub.library.auth.service.util.TokenConfig;
import org.pollub.library.auth.service.util.TokenGenerator;
import org.pollub.library.auth.service.util.TokenValidator;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements ITokenService {
    private final TokenConfig tokenConfig;
    private final TokenClaimsExtractor claimsExtractor;
    private final TokenValidator tokenValidator;
    private final TokenGenerator tokenGenerator;

    @Override
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, User user) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("enabled", user.isEnabled());
        claims.put("id", user.getId());
        return tokenGenerator.generate(claims, user, tokenConfig.getExpirationTime());
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean hasTokenExpired(String token) {
        return tokenValidator.isExpired(token);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsExtractor.extractClaim(token, claimsResolver);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        return tokenValidator.isValid(token, user);
    }

    @Override
    public long getExpirationTime() {
        return tokenConfig.getExpirationTime();
    }
}