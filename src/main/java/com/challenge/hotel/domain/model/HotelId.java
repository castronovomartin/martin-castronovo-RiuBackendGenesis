package com.challenge.hotel.domain.model;

/**
 * Value object representing the unique identifier of a hotel.
 * Ensures the hotel identifier is never null, blank, or empty.
 */
public record HotelId(String value) {

   /**
    * Compact constructor — validates the value upon creation.
    * A HotelId can never exist in an invalid state.
    */
   public HotelId {
      if (value == null || value.isBlank()) {
         throw new IllegalArgumentException("HotelId value must not be null or blank");
      }
   }
}
