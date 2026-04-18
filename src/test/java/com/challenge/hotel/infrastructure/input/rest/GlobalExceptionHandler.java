package com.challenge.hotel.infrastructure.input.rest;

import com.challenge.hotel.domain.model.SearchNotFoundException;
import com.challenge.hotel.domain.model.SearchPublishException;
import com.challenge.hotel.infrastructure.input.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Global exception handler for REST controllers.
 * Maps domain and validation exceptions to appropriate HTTP responses.
 * Returns a standardized ErrorResponse for all error cases.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

   /**
    * Handles Bean Validation errors on @RequestBody — returns HTTP 400.
    * Processes both field-level errors (@NotNull, @NotBlank) and
    * class-level errors (@ValidDateRange) to ensure all messages reach the client.
    *
    * @param ex the validation exception
    * @return HTTP 400 with standardized error response
    */
   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
         final MethodArgumentNotValidException ex) {

      final var binding = ex.getBindingResult();

      final var errors = Stream.concat(
            binding.getFieldErrors().stream()
                   .map(f -> Map.entry(
                         f.getField(),
                         f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid value"
                   )),
            binding.getGlobalErrors().stream()
                   .map(e -> Map.entry(
                         e.getObjectName(),
                         e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                   ))
      ).collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (existing, replacement) -> existing,
            LinkedHashMap::new
      ));

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(ErrorResponse.of("VALIDATION_ERROR", errors.toString()));
   }

   /**
    * Handles Bean Validation errors on @RequestParam — returns HTTP 400.
    *
    * @param ex the constraint violation exception
    * @return HTTP 400 with standardized error response
    */
   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ErrorResponse> handleConstraintViolation(
         final ConstraintViolationException ex) {
      final var message = ex.getConstraintViolations()
                            .stream()
                            .collect(Collectors.toMap(
                                  v -> v.getPropertyPath().toString(),
                                  ConstraintViolation::getMessage
                            ))
                            .toString();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(ErrorResponse.of("VALIDATION_ERROR", message));
   }

   /**
    * Handles SearchNotFoundException — returns HTTP 404.
    *
    * @param ex the search not found exception
    * @return HTTP 404 with standardized error response
    */
   @ExceptionHandler(SearchNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleSearchNotFound(
         final SearchNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
                           .body(ErrorResponse.of("SEARCH_NOT_FOUND", ex.getMessage()));
   }

   /**
    * Handles domain IllegalArgumentException — returns HTTP 400.
    *
    * @param ex the illegal argument exception
    * @return HTTP 400 with standardized error response
    */
   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ErrorResponse> handleIllegalArgument(
         final IllegalArgumentException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(ErrorResponse.of("BAD_REQUEST", ex.getMessage()));
   }

   /**
    * Handles Kafka publish failures — returns HTTP 503.
    * Triggered when the event stream is unavailable or the publish times out.
    *
    * @param ex the publish exception
    * @return HTTP 503 with standardized error response
    */
   @ExceptionHandler(SearchPublishException.class)
   public ResponseEntity<ErrorResponse> handleSearchPublish(
         final SearchPublishException ex) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                           .body(ErrorResponse.of("PUBLISH_ERROR", ex.getMessage()));
   }
}
