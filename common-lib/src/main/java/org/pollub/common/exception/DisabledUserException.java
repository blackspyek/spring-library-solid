package org.pollub.common.exception;

public class DisabledUserException extends RuntimeException {
    public DisabledUserException(String username) {
        super("User is disabled: " + username);
    }
}