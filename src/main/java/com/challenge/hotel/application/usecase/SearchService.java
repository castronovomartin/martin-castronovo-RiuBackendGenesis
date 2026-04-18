package com.challenge.hotel.application.usecase;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.application.port.input.SearchUseCase;
import com.challenge.hotel.domain.port.output.SearchEventPublisher;

/**
 * Application service implementing the search use case.
 * Orchestrates domain objects and output ports to fulfill the search creation.
 * Publishes the search event and returns the generated identifier.
 * Does not persist directly — persistence is handled asynchronously by the Kafka consumer.
 */
public class SearchService implements SearchUseCase {

   private final SearchEventPublisher searchEventPublisher;

   /**
    * Constructor injection — ensures the service is always in a valid state.
    *
    * @param searchEventPublisher the output port for publishing search events
    */
   public SearchService(final SearchEventPublisher searchEventPublisher) {
      this.searchEventPublisher = searchEventPublisher;
   }

   /**
    * Creates a hotel availability search by publishing it to the event stream.
    * The search is persisted asynchronously by the Kafka consumer.
    *
    * @param search the search domain object containing all search parameters
    * @return the generated unique identifier for this search
    */
   @Override
   public SearchId search(final Search search) {
      searchEventPublisher.publish(search);
      return search.searchId();
   }
}
