package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class FullExceptionHandler {
    public static final LocalDateTime TIME_NOW = LocalDateTime.now();

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundHandle(RuntimeException e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", e.getMessage());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Not found: {}", e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExistException.class)
    public ResponseEntity<ErrorResponse> existHandle(RuntimeException e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", e.getMessage());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Exists error: {}", e.getMessage(), e);
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationHandle(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errors, req.getRequestURI());
        log.error("Validation error: {}", errors, e);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> requestHeaderExceptionHandle(MissingRequestHeaderException e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", e.getMessage());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Missing request header: {}", e.getMessage(), e);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> genericExceptionHandle(Throwable e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", "Unexpected error");
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Unexpected error: {}", e.getMessage(), e);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandle(IllegalArgumentException e) {
        log.error("Illegal argument error: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotAvailableException.class, NotValidException.class})
    public ResponseEntity<ErrorResponse> handleAvailable(RuntimeException e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", e.getMessage());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Not available or not valid: {}", e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}