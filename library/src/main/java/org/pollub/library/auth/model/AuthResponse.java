package org.pollub.library.auth.model;

import org.pollub.library.user.model.Role;

public record AuthResponse(String token, Role[] roles, String responseType, String username) {
    private static final String RESPONSE_TYPE = "Bearer";

    public AuthResponse(String token, Role[] roles, String username) {
        this(token, roles, RESPONSE_TYPE, username);
    }
}