package org.pollub.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error(ex.getMessage())
                        .build()
                        .toMap()
        );
        //Lab1 End Builder 2
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(ex.getMessage())
                        .build()
                        .toMap()
        );
        //Lab1 End Builder 2
    }

    @ExceptionHandler(DisabledUserException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledUser(DisabledUserException ex) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(ex.getMessage())
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }

    @ExceptionHandler(FavouriteLibraryNotSetException.class)
    public ResponseEntity<Map<String, Object>> handleFavouriteLibraryNotSet(FavouriteLibraryNotSetException ex) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(ex.getMessage())
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation failed")
                        .details(fieldErrors)
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(ex.getMessage())
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex
    ) {
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error("Access denied")
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        log.error(ex.getMessage(), ex);
        //Lab1 - Builder 2 Start
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal server error" + (ex.getMessage() != null ? ": " + ex.getMessage() : ""))
                        .build()
                        .toMap());
        //Lab1 End Builder 2
    }
}
