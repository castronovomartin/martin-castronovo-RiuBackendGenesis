package com.challenge.hotel.application.port.input;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;

/**
 * Input port defining the contract for creating a hotel availability search.
 * Implemented by the application layer, called by the REST input adapter.
 */
public interface SearchUseCase {

   /**
    * Creates a new hotel availability search and publishes it to the event stream.
    *
    * @param search the search domain object containing all search parameters
    * @return the generated unique identifier for this search
    */
   SearchId search(Search search);
}
