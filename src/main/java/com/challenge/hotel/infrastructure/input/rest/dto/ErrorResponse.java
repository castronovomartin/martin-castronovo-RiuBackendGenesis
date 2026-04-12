package com.challenge.hotel.infrastructure.input.rest.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Standardized error response for all REST API errors.
 * Provides a consistent structure for error handling across all endpoints.
 */
public record ErrorResponse(
      String error,
      String message,
      String timestamp
) {
   private static final DateTimeFormatter FORMATTER =
         DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

   public static ErrorResponse of(final String error, final String message) {
      return new ErrorResponse(
            error,
            message,
            LocalDateTime.now().format(FORMATTER)
      );
   }
}
