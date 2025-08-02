package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class FullExceptionHandler {
    public static final LocalDateTime TIME_NOW = LocalDateTime.now();

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchHandle(
            MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        String textError = String.format("Parameter '%s' value '%s' could not be converted to required type",
                e.getName(), e.getValue());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), Map.of("error", textError), req.getRequestURI());
        log.error("Type mismatch error: {}", textError, e);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationHandle(ConstraintViolationException e, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), errors, req.getRequestURI());
        log.error("Constraint violation error: {}", errors, e);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericExceptionHandle(Exception e, HttpServletRequest req) {
        Map<String, String> textError = Map.of("Error", "Unexpected error");
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), textError, req.getRequestURI());
        log.error("Unexpected error: {}", e.getMessage(), e);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> missingServletRequestParameterHandle(
            MissingServletRequestParameterException e, HttpServletRequest req) {
        String textError = String.format("Required parameter '%s' is not present", e.getParameterName());
        ErrorResponse res = new ErrorResponse(TIME_NOW, HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), Map.of("error", textError), req.getRequestURI());
        log.error("Missing parameter error: {}", textError, e);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}