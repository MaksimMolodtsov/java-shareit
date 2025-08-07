package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullExceptionHandlerTest {

    private FullExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new FullExceptionHandler();
        lenient().when(request.getRequestURI()).thenReturn("/test/endpoint");
    }

    @Test
    void illegalArgumentExceptionHandleShouldReturnBadRequest() {
        String errorMessage = "Invalid argument";
        IllegalArgumentException e = new IllegalArgumentException(errorMessage);
        ResponseEntity<String> response = exceptionHandler.illegalArgumentExceptionHandle(e);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleGenericExceptionShouldReturnInternalServerError() {
        Exception e = new Exception("Unexpected error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.genericExceptionHandle(e, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse body = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), body.getError());
        assertEquals("/test/endpoint", body.getPath());
        assertTrue(body.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                body.getTimestamp().isEqual(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void handleNotFoundExceptionShouldReturnNotFound() {
        String errorMessage = "Not found";
        NotFoundException e = new NotFoundException(errorMessage);
        ResponseEntity<ErrorResponse> response = exceptionHandler.notFoundHandle(e, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());
    }

    @Test
    void existHandleExceptionShouldReturnConflict() {
        String errorMessage = "Already exists";
        ExistException e = new ExistException(errorMessage);
        ResponseEntity<ErrorResponse> response = exceptionHandler.existHandle(e, request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());
    }

    @Test
    void requestHeaderExceptionHandleShouldReturnBadRequest() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.getNestedParameterType()).thenReturn((Class) String.class);
        String headerName = "X-Header";
        MissingRequestHeaderException e = new MissingRequestHeaderException(headerName, parameter);
        ResponseEntity<ErrorResponse> response = exceptionHandler.requestHeaderExceptionHandle(e, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse body = response.getBody();
        assertEquals("/test/endpoint", body.getPath());
    }

    @Test
    void handleAvailableShouldReturnBadRequest() {
        String errorMessage = "Not available";
        NotAvailableException e = new NotAvailableException(errorMessage);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAvailable(e, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}