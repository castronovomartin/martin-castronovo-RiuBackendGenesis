package com.challenge.hotel.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.challenge.hotel.domain.model.Search;

import java.time.LocalDate;
import java.util.List;

/**
 * Immutable Data Transfer Object representing the search details
 * nested inside the GET /count response.
 * Serializes LocalDate fields to dd/MM/yyyy format via @JsonFormat.
 */
public record SearchDetailDTO(

      String hotelId,

      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkIn,

      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkOut,

      List<Integer> ages

) {

   /**
    * Factory method — creates a SearchDetailDTO from a domain Search object.
    * Extracts fields from value objects — no logic in getters.
    *
    * @param search the domain search object
    * @return a new SearchDetailDTO
    */
   public static SearchDetailDTO from(final Search search) {
      return new SearchDetailDTO(
            search.hotelId().value(),
            search.dateRange().checkIn(),
            search.dateRange().checkOut(),
            search.ages().values()
      );
   }
}