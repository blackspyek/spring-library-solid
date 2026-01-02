package org.pollub.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class InternalAuthFilter extends OncePerRequestFilter {

    private final String internalSecret;
    private static final String HEADER_NAME = "X-Internal-Auth";
    private static final String INTERNAL_ROLE = "ROLE_INTERNAL_SERVICE";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String headerValue = request.getHeader(HEADER_NAME);

            if (StringUtils.hasText(headerValue) && headerValue.equals(internalSecret)) {
                // Determine which service is calling if possible, or just use generic internal user
                // For now, we grant specific internal privileges
                log.debug("Internal service authentication successful");
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        "INTERNAL_SERVICE",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(INTERNAL_ROLE))
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Internal auth error provided", e);
        }

        filterChain.doFilter(request, response);
    }
}
