package com.challenge.hotel.infrastructure.output.kafka;

import com.challenge.hotel.domain.model.Search;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Kafka message representing a hotel availability search event.
 * Defines the contract for messages published to hotel_availability_searches topic.
 * Immutable record — serialized to JSON by Jackson for Kafka transport.
 */
public record SearchMessage(

      String searchId,
      String hotelId,

      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkIn,

      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkOut,

      List<Integer> ages

) {

   /**
    * Factory method — creates a SearchMessage from a domain Search object.
    *
    * @param search the domain search object
    * @return a new SearchMessage ready for Kafka transport
    */
   public static SearchMessage from(final Search search) {
      return new SearchMessage(
            search.searchId().value(),
            search.hotelId().value(),
            search.dateRange().checkIn(),
            search.dateRange().checkOut(),
            List.copyOf(search.ages().values())
      );
   }
}
