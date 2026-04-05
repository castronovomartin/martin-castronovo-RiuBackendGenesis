package com.challenge.hotel.domain.port.output;

import com.challenge.hotel.domain.model.Search;

/**
 * Output port defining the contract for publishing search events.
 * Implemented by the infrastructure layer using Kafka.
 * The domain defines what it needs — infrastructure decides how to do it.
 */
public interface SearchEventPublisher {

   /**
    * Publishes a search event to the event stream.
    * Called after a new search is created to trigger async persistence.
    *
    * @param search the search event to publish
    */
   void publish(Search search);
}
