package io.github.vldr2212.geocodercompare.exception;

import io.github.vldr2212.geocodercompare.constants.ErrorMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений: преобразует исключения в {@link ProblemDetail}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== BUSINESS ==========
    @ExceptionHandler(AddressNotFoundException.class)
    public ProblemDetail handleAddressNotFound(AddressNotFoundException e) {
        log.debug("Address not found by some geocoder: {}", e.getAddress());
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException e) {
        log.debug("Resource not found: {}", e.getIdentifier());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(GeocoderUnavailableException.class)
    public ProblemDetail handleGeocoderUnavailable(GeocoderUnavailableException e) {
        log.warn("Geocoder unavailable: provider={}", e.getProvider(), e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, e.getMessage());
        problemDetail.setProperty("provider", e.getProvider());
        return problemDetail;
    }

    // ========== REQUEST ==========
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        log.debug("Validation failed: {}", errors);
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ErrorMessages.VALIDATION_FAILED);
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.debug("Message not readable: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_REQUEST_BODY);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.debug("Type mismatch for parameter '{}'", e.getName());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_PARAMETER_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.debug("Method not supported: {}", e.getMethod());
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, ErrorMessages.METHOD_NOT_ALLOWED);
    }

    // ========== GENERAL ==========
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_ERROR);
    }
}