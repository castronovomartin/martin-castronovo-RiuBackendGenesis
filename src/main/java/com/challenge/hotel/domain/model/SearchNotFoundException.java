package com.challenge.hotel.domain.model;

/**
 * Domain exception thrown when a search cannot be found by its identifier.
 * Indicates a business rule violation — the requested search does not exist.
 */
public class SearchNotFoundException extends RuntimeException {

   public SearchNotFoundException(final SearchId searchId) {
      super("No search found for searchId: %s".formatted(searchId.value()));
   }
}