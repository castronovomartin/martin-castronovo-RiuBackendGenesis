package com.challenge.hotel.application.usecase;

import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.port.input.CountUseCase;
import com.challenge.hotel.domain.port.output.SearchRepository;

/**
 * Application service implementing the count use case.
 * Retrieves the reference search and counts how many equal searches exist.
 * Age order is significant in equality comparison.
 */
public class CountService implements CountUseCase {

   private final SearchRepository searchRepository;

   /**
    * Constructor injection — ensures the service is always in a valid state.
    *
    * @param searchRepository the output port for search persistence
    */
   public CountService(final SearchRepository searchRepository) {
      this.searchRepository = searchRepository;
   }

   /**
    * Counts how many searches are equal to the one identified by the given searchId.
    *
    * @param searchId the identifier of the reference search
    * @return CountResult containing the reference search and the count of equal searches
    * @throws IllegalArgumentException if no search is found for the given searchId
    */
   @Override
   public CountResult count(final SearchId searchId) {
      final var search = searchRepository.findById(searchId)
                                         .orElseThrow(() -> new IllegalArgumentException(
                                               "No search found for searchId: %s".formatted(searchId.value())));
      final var count = searchRepository.countEquals(search);
      return new CountResult(search, count);
   }
}
