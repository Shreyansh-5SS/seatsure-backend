package com.seatsure.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice // Tells Spring: "I am the global error catcher!"
public class GlobalExceptionHandler {

    // Tells Spring: "If ANY controller throws this specific error, route it to me!"
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(EmailAlreadyExistsException ex) {
        // We build a clean JSON response instead of a massive Java stack trace
        Map<String, String> response = Map.of(
                "error", "Conflict",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // Returns a clean 409 status code
    }
}