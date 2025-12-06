package org.pollub.library.exception.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.DisabledUserException;
import org.pollub.library.exception.InvalidCredentialsException;
import org.pollub.library.exception.ResourceNotFoundException;
import org.pollub.library.exception.util.ErrorResponseGenerator;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
@Order(1)
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseGenerator errorResponseGenerator;
    private static final String ERROR_MESSAGE = "An unexpected error occurred";
    private static final String ERROR = "error";

    @ExceptionHandler({InvalidCredentialsException.class})
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse("Authentication failed", Collections.singletonMap(ERROR, Collections.singletonList(ex.getMessage())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse("Authentication failed", Collections.singletonMap(ERROR, Collections.singletonList("Nieprawidłowy email lub hasło")), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({DisabledUserException.class})
    public ResponseEntity<String> handleDisabledUserException(DisabledUserException ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse("Account not activated", Collections.singletonMap(ERROR, Collections.singletonList("Konto nie zostało jeszcze aktywowane. Sprawdź swoją skrzynkę email.")), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<String> handleResourceNotFoundException(Exception ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse(ERROR_MESSAGE, Collections.singletonMap(ERROR, Collections.singletonList(ex.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> handleForbiddenException(Exception ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse(ERROR_MESSAGE, Collections.singletonMap(ERROR, Collections.singletonList(ex.getMessage())), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleAnyRuntimeException(Exception ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse(ERROR_MESSAGE, Collections.singletonMap(ERROR, Collections.singletonList(ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAnyException(Exception ex) throws JsonProcessingException {
        return errorResponseGenerator.generateResponse(ERROR_MESSAGE, Collections.singletonMap(ERROR, Collections.singletonList(ex.getMessage())), HttpStatus.BAD_REQUEST);
    }

}
