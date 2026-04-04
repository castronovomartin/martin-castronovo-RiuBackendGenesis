package com.challenge.hotel.domain.model;

/**
 * Aggregate root representing a hotel availability search.
 * Composed of value objects that enforce their own invariants.
 * Search identity is defined by its SearchId — two searches with the same
 * data but different SearchId are considered different searches.
 */
public record Search(
      SearchId searchId,
      HotelId hotelId,
      DateRange dateRange,
      Ages ages
) {

   /**
    * Compact constructor — validates that no dependency is null.
    * Value objects are responsible for validating their own content.
    *
    * @throws IllegalArgumentException if any field is null
    */
   public Search {
      if (searchId == null) {
         throw new IllegalArgumentException("SearchId must not be null");
      }
      if (hotelId == null) {
         throw new IllegalArgumentException("HotelId must not be null");
      }
      if (dateRange == null) {
         throw new IllegalArgumentException("DateRange must not be null");
      }
      if (ages == null) {
         throw new IllegalArgumentException("Ages must not be null");
      }
   }

   /**
    * Factory method — creates a new Search with a generated SearchId.
    * This is the primary way to create a Search from an incoming request.
    *
    * @param hotelId   the hotel identifier
    * @param dateRange the check-in and check-out date range
    * @param ages      the ages of the guests
    * @return a new Search with a unique generated SearchId
    */
   public static Search create(HotelId hotelId, DateRange dateRange, Ages ages) {
      return new Search(SearchId.generate(), hotelId, dateRange, ages);
   }
}
