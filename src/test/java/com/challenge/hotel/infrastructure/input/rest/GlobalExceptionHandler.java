package com.challenge.hotel.infrastructure.input.rest;

import java.util.stream.Collectors;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import com.challenge.hotel.domain.model.SearchNotFoundException;

/**
 * Global exception handler for REST controllers.
 * Maps domain and validation exceptions to appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

   /**
    * Handles Bean Validation errors on @RequestBody — returns HTTP 400.
    *
    * @param ex the validation exception
    * @return HTTP 400 with error details
    */
   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(
         final MethodArgumentNotValidException ex) {
      final var errors = ex.getBindingResult()
                           .getFieldErrors()
                           .stream()
                           .collect(Collectors.toMap(
                                 FieldError::getField,
                                 field -> field.getDefaultMessage() != null
                                       ? field.getDefaultMessage()
                                       : "Invalid value"
                           ));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
   }

   /**
    * Handles Bean Validation errors on @RequestParam — returns HTTP 400.
    *
    * @param ex the constraint violation exception
    * @return HTTP 400 with error message
    */
   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<Map<String, String>> handleConstraintViolation(
         final ConstraintViolationException ex) {
      final var errors = ex.getConstraintViolations()
                           .stream()
                           .collect(Collectors.toMap(
                                 v -> v.getPropertyPath().toString(),
                                 ConstraintViolation::getMessage
                           ));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
   }

   /**
    * Handles domain IllegalArgumentException — returns HTTP 400.
    *
    * @param ex the illegal argument exception
    * @return HTTP 400 with error message
    */
   @ExceptionHandler(SearchNotFoundException.class)
   public ResponseEntity<Map<String, String>> handleSearchNotFound(
         final SearchNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                           .body(Map.of("error", ex.getMessage()));
   }
}
