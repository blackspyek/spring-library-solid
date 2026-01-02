package org.pollub.user.exception.handlers;

import org.pollub.user.exception.DisabledUserException;
import org.pollub.user.exception.FavouriteLibraryNotSetException;
import org.pollub.user.exception.UserAlreadyExistsException;
import org.pollub.user.exception.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Registration Error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DisabledUserException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledUser(DisabledUserException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "User Disabled");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(FavouriteLibraryNotSetException.class)
    public ResponseEntity<Map<String, Object>> handleFavouriteLibraryNotSet(FavouriteLibraryNotSetException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Favourite Library Not Set");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}