package com.project.library.handlers;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.library.dto.CustomErrorDTO;
import com.project.library.exceptions.EntityNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorDTO> handleGeneralException(Exception exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred";

        CustomErrorDTO dto = new CustomErrorDTO(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomErrorDTO> handleEntityNotFound(EntityNotFoundException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = exception.getMessage();

        CustomErrorDTO dto = new CustomErrorDTO(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDTO> handleValidation(MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Validation failed for one or more fields";

        List<CustomErrorDTO.FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new CustomErrorDTO.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        CustomErrorDTO dto = new CustomErrorDTO(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI(),
                fieldErrors);

        return ResponseEntity.status(status).body(dto);
    }
}