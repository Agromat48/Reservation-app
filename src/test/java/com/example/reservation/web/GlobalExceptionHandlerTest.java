package com.example.reservation.web;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGenericException_returns500WithInternalServerErrorLabel() {
        Exception ex = new RuntimeException("boom");

        ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Iternal server error", response.getBody().message());
        assertEquals("boom", response.getBody().detailedMessage());
        assertNotNull(response.getBody().errorTime());
    }

    @Test
    void handleEntityNotFound_returns404WithEntityNotFoundLabel() {
        EntityNotFoundException ex = new EntityNotFoundException("Reservation with id 1 not found!");

        ResponseEntity<ErrorResponseDto> response = handler.handleEntityNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody().message());
        assertEquals("Reservation with id 1 not found!", response.getBody().detailedMessage());
        assertNotNull(response.getBody().errorTime());
    }

    @Test
    void handleBadRequest_whenIllegalArgumentException_returns400WithBadRequestLabel() {
        IllegalArgumentException ex = new IllegalArgumentException("Start date should be after end date");

        ResponseEntity<ErrorResponseDto> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().message());
        assertEquals("Start date should be after end date", response.getBody().detailedMessage());
        assertNotNull(response.getBody().errorTime());
    }

    @Test
    void handleBadRequest_whenIllegalStateException_returns400WithBadRequestLabel() {
        IllegalStateException ex = new IllegalStateException("Cannot approve reservation because of conflict");

        ResponseEntity<ErrorResponseDto> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().message());
        assertEquals("Cannot approve reservation because of conflict", response.getBody().detailedMessage());
    }

    @Test
    void handleBadRequest_whenMethodArgumentNotValidException_returns400WithBadRequestLabel() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("handleBadRequest_whenMethodArgumentNotValidException_returns400WithBadRequestLabel"),
                -1
        );
        var bindingResult = new BeanPropertyBindingResult(new Object(), "reservation");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().message());
        assertNotNull(response.getBody().errorTime());
    }
}
