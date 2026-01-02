package org.pollub.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter that validates tokens locally.
 * Each service uses this filter to authenticate requests without calling auth-service.
 */
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token)) {
                try {
                    jwtTokenProvider.validateTokenAndThrow(token);
                    
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);
                    java.util.List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                    
                    // Create authentication with user details
                    JwtUserDetails userDetails = new JwtUserDetails(userId, username, roles);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Add user info to request attributes for easy access in controllers
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                    // Use primary role for backward compatibility or set roles list
                    if (!roles.isEmpty()) {
                        request.setAttribute("role", roles.get(0));
                    }
                    request.setAttribute("roles", roles);
                    
                } catch (Exception e) {
                    log.error("Cannot set user authentication: {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                    request.setAttribute("exception", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. Try to get from Cookie (Priority)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. Fallback to Header
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
