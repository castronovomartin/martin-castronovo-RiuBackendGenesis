package com.challenge.hotel.domain.model;

import java.time.LocalDate;

/**
 * Value object representing a date range for a hotel stay.
 * Enforces the invariant that check-in date must be strictly before check-out date.
 * Dates are stored as LocalDate — no time zone concerns, no deprecated Date usage.
 */
public record DateRange(LocalDate checkIn, LocalDate checkOut) {

   /**
    * Compact constructor — validates the date range upon creation.
    * A DateRange can never exist in an invalid state.
    *
    * @throws IllegalArgumentException if either date is null or checkIn is not before checkOut
    */
   public DateRange {
      if (checkIn == null) {
         throw new IllegalArgumentException("Check-in date must not be null");
      }
      if (checkOut == null) {
         throw new IllegalArgumentException("Check-out date must not be null");
      }
      if (!checkIn.isBefore(checkOut)) {
         throw new IllegalArgumentException(
               "Check-in date must be strictly before check-out date");
      }
   }
}
