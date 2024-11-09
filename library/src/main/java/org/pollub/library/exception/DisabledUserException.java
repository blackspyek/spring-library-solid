package org.pollub.library.exception;

public class DisabledUserException extends RuntimeException {
    public DisabledUserException(String username) {
        super("User is disabled: " + username);
    }
}