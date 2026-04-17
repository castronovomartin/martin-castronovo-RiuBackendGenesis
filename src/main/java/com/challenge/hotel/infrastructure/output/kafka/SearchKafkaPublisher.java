package com.challenge.hotel.infrastructure.output.kafka;

import java.util.concurrent.ExecutionException;

import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchPublishException;
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
    * Blocks until Kafka confirms receipt — prevents silent failures.
    * Uses searchId as the message key for partition consistency.
    *
    * @param search the domain search object to publish
    * @throws SearchPublishException if Kafka fails to receive the message
    */
   @Override
   public void publish(final Search search) {
      final var message = SearchMessage.from(search);
      try {
         kafkaTemplate.send(TOPIC, message.searchId(), message).get();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new SearchPublishException(search.searchId().value(), e);
      } catch (ExecutionException e) {
         throw new SearchPublishException(search.searchId().value(), e.getCause());
      }
   }
}
