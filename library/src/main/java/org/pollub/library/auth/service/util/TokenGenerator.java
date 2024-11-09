package org.pollub.library.auth.service.util;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.pollub.library.user.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final TokenConfig tokenConfig;

    public String generate(Map<String, Object> claims, User user, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(tokenConfig.getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}
