package org.pollub.library.exception.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.util.ErrorResponseGenerator;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Order(0)
@RequiredArgsConstructor
public class ValidationExceptionHandler {

    private final ErrorResponseGenerator errorResponseGenerator;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, List<String>> errors = extractFieldErrors(ex.getBindingResult());
        return errorResponseGenerator.generateResponse("Validation failed", errors, HttpStatus.BAD_REQUEST);
    }


    private Map<String, List<String>> extractFieldErrors(BindingResult bindingResult) {
        Map<String, List<String>> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.computeIfAbsent(fieldError.getField(), _ -> new ArrayList<>()).add(fieldError.getDefaultMessage());
        }

        for (ObjectError objectError : bindingResult.getGlobalErrors()) {
            errors.computeIfAbsent("message", _ -> new ArrayList<>()).add(objectError.getDefaultMessage());
        }

        return errors;
    }
}