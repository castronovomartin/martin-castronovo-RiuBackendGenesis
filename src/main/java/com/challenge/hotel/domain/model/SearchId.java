package com.challenge.hotel.domain.model;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a hotel availability search.
 * The ID is generated algorithmically without database access, ensuring
 * uniqueness through UUID v4 random generation.
 */
public record SearchId(String value) {

   /**
    * Compact constructor — validates the value upon creation.
    * A SearchId can never exist in an invalid state.
    */
   public SearchId {
      if (value == null || value.isBlank()) {
         throw new IllegalArgumentException("SearchId value must not be null or blank");
      }
   }

   /**
    * Factory method — generates a new unique SearchId without database access.
    * Uses UUID v4 which guarantees uniqueness through random generation.
    *
    * @return a new unique SearchId
    */
   public static SearchId generate() {
      return new SearchId(UUID.randomUUID().toString());
   }
}
