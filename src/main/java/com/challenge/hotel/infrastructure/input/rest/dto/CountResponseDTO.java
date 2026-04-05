package com.challenge.hotel.infrastructure.input.rest.dto;

import com.challenge.hotel.domain.port.input.CountUseCase.CountResult;

/**
 * Immutable Data Transfer Object representing the HTTP response body for GET /count.
 * Contains the search identifier, search details and count of equal searches.
 */
public record CountResponseDTO(
      String searchId,
      SearchDetailDTO search,
      long count
) {

   /**
    * Factory method — creates a CountResponseDTO from a domain CountResult.
    *
    * @param searchId the search identifier string
    * @param result   the domain count result containing search and count
    * @return a new CountResponseDTO
    */
   public static CountResponseDTO from(final String searchId, final CountResult result) {
      return new CountResponseDTO(
            searchId,
            SearchDetailDTO.from(result.search()),
            result.count()
      );
   }
}
