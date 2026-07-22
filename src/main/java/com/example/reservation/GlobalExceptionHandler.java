package com.example.reservation;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponceDto> handleGenericException(Exception ex) {
        log.error("Handle exception", ex);

        var errorDto = new ErrorResponceDto(
                "Iternal server error",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponceDto> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Handle entityNotFoundException", ex);

        var errorDto = new ErrorResponceDto(
                "Entity not found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponceDto> handleBadRequest(Exception ex) {
        log.error("Handle IllegalArgumentException", ex);

        var errorDto = new ErrorResponceDto(
                "Bad request",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }
}
