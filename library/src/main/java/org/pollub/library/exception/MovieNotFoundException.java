package org.pollub.library.exception;

public class MovieNotFoundException extends ResourceNotFoundException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
