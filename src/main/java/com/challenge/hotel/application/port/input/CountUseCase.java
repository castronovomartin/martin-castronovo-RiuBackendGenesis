package com.challenge.hotel.application.port.input;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;

/**
 * Input port defining the contract for counting equal hotel availability searches.
 * Implemented by the application layer, called by the REST input adapter.
 */
public interface CountUseCase {

   /**
    * Counts how many searches are equal to the one identified by the given searchId.
    * Age order is significant — searches with same ages in different order are not equal.
    *
    * @param searchId the identifier of the reference search
    * @return the search object and the count of equal searches
    */
   CountResult count(SearchId searchId);

   /**
    * Immutable result object containing the search details and the count of equal searches.
    *
    * @param search the search associated with the given searchId
    * @param count  the number of searches equal to the reference search
    */
   record CountResult(Search search, long count) {
      public CountResult {
         if (search == null) {
            throw new IllegalArgumentException("Search must not be null");
         }
         if (count < 0) {
            throw new IllegalArgumentException("Count must not be negative");
         }
      }
   }
}