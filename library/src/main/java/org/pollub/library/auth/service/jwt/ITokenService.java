package org.pollub.library.auth.service.jwt;

import io.jsonwebtoken.Claims;
import org.pollub.library.user.model.User;
import org.springframework.http.ResponseCookie;

import java.util.Map;
import java.util.function.Function;

public interface ITokenService {
    String generateToken(User user);
    String generateToken(Map<String, Object> extraClaims, User user);

    boolean isTokenValid(String token, User user);
    String extractUsername(String token);
    boolean hasTokenExpired(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    long getExpirationTime();
}
