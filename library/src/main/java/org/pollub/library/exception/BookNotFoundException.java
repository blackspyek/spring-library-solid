package org.pollub.library.exception;


public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
