package org.pollub.library.exception.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ErrorResponseGenerator {

    private final ObjectMapper objectMapper;

    public ResponseEntity<String> generateResponse(String message, Object errors, HttpStatus status) throws JsonProcessingException {
        Map<String, Object> errorMap = Map.of(
                "error", status.getReasonPhrase(),
                "message", message,
                "status", status.value(),
                "errors", errors
        );

        String jsonResponse = objectMapper.writeValueAsString(errorMap);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }
}
