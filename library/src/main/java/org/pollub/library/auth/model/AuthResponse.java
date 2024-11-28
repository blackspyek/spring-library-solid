package org.pollub.library.auth.model;


public record AuthResponse(String token, String responseType, String username) {
    private static final String RESPONSE_TYPE = "Bearer";

    public AuthResponse(String token, String username) {
        this(token, RESPONSE_TYPE, username);
    }
}
