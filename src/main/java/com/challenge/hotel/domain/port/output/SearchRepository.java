package com.challenge.hotel.domain.port.output;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;

import java.util.Optional;

/**
 * Output port defining the contract for search persistence.
 * Implemented by the infrastructure layer using JPA and Oracle.
 * The domain defines what it needs — infrastructure decides how to do it.
 */
public interface SearchRepository {

   /**
    * Persists a new hotel availability search.
    *
    * @param search the search to persist
    */
   void save(Search search);

   /**
    * Finds a search by its unique identifier.
    *
    * @param searchId the identifier of the search to find
    * @return an Optional containing the search if found, empty otherwise
    */
   Optional<Search> findById(SearchId searchId);

   /**
    * Counts how many searches are equal to the given search.
    * Equality considers hotelId, dateRange and ages in order.
    *
    * @param search the reference search to compare against
    * @return the number of equal searches found
    */
   long countEquals(Search search);
}
