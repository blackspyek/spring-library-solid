package org.pollub.library.exception;

/**
 * Exception thrown when login credentials are invalid.
 * This is a generic exception used to hide whether the email doesn't exist
 * or the password is incorrect (security best practice).
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Nieprawidłowy email lub hasło");
    }
}
