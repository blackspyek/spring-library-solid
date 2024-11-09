package org.pollub.library.config.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.pollub.library.auth.service.jwt.JwtTokenService;
import org.pollub.library.user.model.User;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final ErrorResponseBuilder errorResponseBuilder;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            processAuthentication(request, response, filterChain);
        } catch (Exception e) {
            errorResponseBuilder.buildAndSendErrorResponse(
                    response,
                    "Authentication failed: " + e.getMessage()
            );
        }
    }

    private void processAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        String jwt = TokenExtractor.extractFromRequest(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        authenticateToken(jwt, request, response, filterChain);
    }

    private void authenticateToken(
            String jwt,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        if (tokenService.hasTokenExpired(jwt)) {
            errorResponseBuilder.buildAndSendErrorResponse(response, "Token has expired");
            return;
        }

        String username = tokenService.extractUsername(jwt);
        if (!isValidAuthenticationContext(username)) {
            errorResponseBuilder.buildAndSendErrorResponse(response, "Invalid authentication context");
            return;
        }

        User user = loadAndValidateUser(jwt, username, response);
        if (user == null) {
            return;
        }

        createSecurityContext(user, request);
        filterChain.doFilter(request, response);
    }

    private User loadAndValidateUser(
            String jwt,
            String username,
            HttpServletResponse response
    ) throws IOException {
        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!tokenService.isTokenValid(jwt, user)) {
            errorResponseBuilder.buildAndSendErrorResponse(response, "Token is not valid");
            return null;
        }
        return user;
    }

    private boolean isValidAuthenticationContext(String username) {
        if (username == null) {
            return false;
        }
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return existingAuth == null;
    }

    private void createSecurityContext(User user, HttpServletRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}