package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse createErrorResponse(Exception e, HttpStatus status, HttpServletRequest request) {
        return new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                status.value(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException e, HttpServletRequest request) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.NOT_FOUND, request);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.CONFLICT, request);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException e, HttpServletRequest request) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.BAD_REQUEST, request);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, HttpServletRequest request) {
        ErrorResponse response = createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}