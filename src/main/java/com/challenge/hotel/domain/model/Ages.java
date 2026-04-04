package com.challenge.hotel.domain.model;

import java.util.List;

/**
 * Value object representing the ages of the guests in a hotel search.
 * Preserves insertion order — order affects search count matching.
 * The internal list is defensively copied and made immutable upon creation,
 * preventing external mutation after object construction.
 */
public record Ages(List<Integer> values) {

   /**
    * Compact constructor — defensively copies and makes the list immutable.
    * Validates that the list is not null or empty.
    *
    * @throws IllegalArgumentException if ages list is null or empty
    */
   public Ages {
      if (values == null || values.isEmpty()) {
         throw new IllegalArgumentException("Ages list must not be null or empty");
      }
      values = List.copyOf(values);
   }
}
