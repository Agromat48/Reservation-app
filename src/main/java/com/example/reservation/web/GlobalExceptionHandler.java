package com.example.reservation.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Handle exception", ex);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Iternal server error", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Handle entityNotFoundException", ex);

        return buildErrorResponse(HttpStatus.NOT_FOUND, "Entity not found", ex.getMessage());
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception ex) {
        log.error("Handle IllegalArgumentException", ex);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String error, String message) {
        var errorDto = new ErrorResponseDto(error, message, LocalDateTime.now());
        return ResponseEntity.status(status).body(errorDto);
    }
}
