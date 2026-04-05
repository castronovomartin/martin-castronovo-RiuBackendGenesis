package com.challenge.hotel.infrastructure.input.rest.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link ValidDateRange} annotation.
 * Validates that checkIn date is strictly before checkOut date.
 */
public class DateRangeValidator
      implements ConstraintValidator<ValidDateRange, SearchRequestDTO> {

   /**
    * Validates that checkIn is strictly before checkOut.
    * Returns true if either date is null — null validation is handled
    * by @NotNull annotations on the individual fields.
    *
    * @param dto     the SearchRequestDTO to validate
    * @param context the constraint validator context
    * @return true if the date range is valid, false otherwise
    */
   @Override
   public boolean isValid(
         final SearchRequestDTO dto,
         final ConstraintValidatorContext context) {
      if (dto.checkIn() == null || dto.checkOut() == null) {
         return true;
      }
      return dto.checkIn().isBefore(dto.checkOut());
   }
}
