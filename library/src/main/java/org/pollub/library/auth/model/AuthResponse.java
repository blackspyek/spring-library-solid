package org.pollub.library.auth.model;


public record AuthResponse(String token, String responseType) {
    private static final String RESPONSE_TYPE = "Bearer";

    public AuthResponse(String token) {
        this(token, RESPONSE_TYPE);
    }
}
