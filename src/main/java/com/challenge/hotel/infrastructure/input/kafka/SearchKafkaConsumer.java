package com.challenge.hotel.infrastructure.input.kafka;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.port.output.SearchRepository;
import com.challenge.hotel.infrastructure.output.kafka.SearchMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka input adapter that consumes hotel availability search events.
 * Persists search data to Oracle database using virtual threads.
 * Clearly separated from the producer — single responsibility per class.
 */
@Component
public class SearchKafkaConsumer {

   private static final Logger log = LoggerFactory.getLogger(SearchKafkaConsumer.class);

   private final SearchRepository searchRepository;

   /**
    * Constructor injection — ensures consumer is always in a valid state.
    *
    * @param searchRepository the output port for search persistence
    */
   public SearchKafkaConsumer(final SearchRepository searchRepository) {
      this.searchRepository = searchRepository;
   }

   /**
    * Consumes search events from the hotel_availability_searches topic.
    * Converts the Kafka message to a domain object and persists it.
    * Virtual threads handle persistence without blocking the consumer thread.
    *
    * @param message the search message received from Kafka
    */
   @KafkaListener(
         topics = "hotel_availability_searches",
         groupId = "hotel-availability-group"
   )
   public void consume(final SearchMessage message) {
      log.info("Received search event for searchId: {}", message.searchId());
      final var search = toDomain(message);
      searchRepository.save(search);
      log.info("Persisted search event for searchId: {}", message.searchId());
   }

   /**
    * Converts a Kafka SearchMessage to a domain Search object.
    * Reconstruction uses the existing searchId — does not generate a new one.
    *
    * @param message the Kafka message to convert
    * @return the domain Search object
    */
   private Search toDomain(final SearchMessage message) {
      return new Search(
            new SearchId(message.searchId()),
            new HotelId(message.hotelId()),
            new DateRange(message.checkIn(), message.checkOut()),
            new Ages(message.ages())
      );
   }
}
