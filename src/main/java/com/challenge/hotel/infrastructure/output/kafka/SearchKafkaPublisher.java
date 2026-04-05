package com.challenge.hotel.infrastructure.output.kafka;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.port.output.SearchEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka output adapter implementing the SearchEventPublisher output port.
 * Publishes search events to the hotel_availability_searches Kafka topic.
 * Separates producer concerns from consumer concerns as required.
 */
@Component
public class SearchKafkaPublisher implements SearchEventPublisher {

   private static final String TOPIC = "hotel_availability_searches";

   private final KafkaTemplate<String, SearchMessage> kafkaTemplate;

   /**
    * Constructor injection — ensures publisher is always in a valid state.
    *
    * @param kafkaTemplate the Spring Kafka template for message publishing
    */
   public SearchKafkaPublisher(final KafkaTemplate<String, SearchMessage> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
   }

   /**
    * Publishes a search event to the Kafka topic.
    * Uses searchId as the message key for partition consistency.
    *
    * @param search the domain search object to publish
    */
   @Override
   public void publish(final Search search) {
      final var message = SearchMessage.from(search);
      kafkaTemplate.send(TOPIC, message.searchId(), message);
   }
}
