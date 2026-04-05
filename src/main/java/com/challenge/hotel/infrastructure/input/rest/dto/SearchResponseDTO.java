package com.challenge.hotel.infrastructure.input.rest.dto;

import com.challenge.hotel.domain.model.SearchId;

/**
 * Immutable Data Transfer Object representing the HTTP response body for POST /search.
 * Contains only the generated search identifier.
 */
public record SearchResponseDTO(String searchId) {

   /**
    * Factory method — creates a SearchResponseDTO from a domain SearchId.
    *
    * @param searchId the domain search identifier
    * @return a new SearchResponseDTO
    */
   public static SearchResponseDTO from(final SearchId searchId) {
      return new SearchResponseDTO(searchId.value());
   }
}
