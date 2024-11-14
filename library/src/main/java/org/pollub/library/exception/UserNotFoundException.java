package org.pollub.library.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
