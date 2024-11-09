package org.pollub.library.exception;

public class TokenConfigurationException extends RuntimeException {
    public TokenConfigurationException(String message) {
        super(message);
    }

    public TokenConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
