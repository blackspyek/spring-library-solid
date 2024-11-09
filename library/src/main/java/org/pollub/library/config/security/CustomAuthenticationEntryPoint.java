package org.pollub.library.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARACTER_ENCODING_UTF8 = "UTF-8";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARACTER_ENCODING_UTF8);
        response.getWriter().write(
                String.format("{\"error\": \"%s\", \"message\": \"%s\"}", UNAUTHORIZED_ERROR_MESSAGE, authException.getMessage())
        );
    }
}

