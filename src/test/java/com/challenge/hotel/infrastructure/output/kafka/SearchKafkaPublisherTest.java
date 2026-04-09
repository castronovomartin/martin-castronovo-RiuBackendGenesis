package com.challenge.hotel.infrastructure.output.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for SearchKafkaPublisher output adapter.
 * Verifies that search events are correctly published to Kafka topic.
 */
@ExtendWith(MockitoExtension.class)
class SearchKafkaPublisherTest {

   @Mock
   private KafkaTemplate<String, SearchMessage> kafkaTemplate;

   private SearchKafkaPublisher publisher;

   private static final SearchId SEARCH_ID = new SearchId("test-uuid-123");
   private static final HotelId HOTEL_ID = new HotelId("1234aBc");
   private static final DateRange DATE_RANGE = new DateRange(
         LocalDate.of(2023, 12, 29),
         LocalDate.of(2023, 12, 31)
   );
   private static final Ages AGES = new Ages(List.of(30, 29, 1, 3));
   private static final Search SEARCH = new Search(SEARCH_ID, HOTEL_ID, DATE_RANGE, AGES);

   @BeforeEach
   void setUp() {
      publisher = new SearchKafkaPublisher(kafkaTemplate);
   }

   @Test
   @DisplayName("Should publish message to correct Kafka topic")
   void shouldPublishMessageToCorrectKafkaTopic() {
      publisher.publish(SEARCH);
      final var topicCaptor = ArgumentCaptor.forClass(String.class);
      final var keyCaptor = ArgumentCaptor.forClass(String.class);
      final var messageCaptor = ArgumentCaptor.forClass(SearchMessage.class);
      verify(kafkaTemplate).send(
            topicCaptor.capture(),
            keyCaptor.capture(),
            messageCaptor.capture()
      );
      assertThat(topicCaptor.getValue()).isEqualTo("hotel_availability_searches");
   }

   @Test
   @DisplayName("Should use searchId as Kafka message key")
   void shouldUseSearchIdAsKafkaMessageKey() {
      publisher.publish(SEARCH);
      final var keyCaptor = ArgumentCaptor.forClass(String.class);
      verify(kafkaTemplate).send(
            anyString(),
            keyCaptor.capture(),
            any()
      );
      assertThat(keyCaptor.getValue()).isEqualTo(SEARCH_ID.value());
   }

   @Test
   @DisplayName("Should publish message with correct search data")
   void shouldPublishMessageWithCorrectSearchData() {
      publisher.publish(SEARCH);
      final var messageCaptor = ArgumentCaptor.forClass(SearchMessage.class);
      verify(kafkaTemplate).send(
            anyString(),
            anyString(),
            messageCaptor.capture()
      );
      final var message = messageCaptor.getValue();
      assertThat(message.searchId()).isEqualTo(SEARCH_ID.value());
      assertThat(message.hotelId()).isEqualTo(HOTEL_ID.value());
      assertThat(message.checkIn()).isEqualTo(DATE_RANGE.checkIn());
      assertThat(message.checkOut()).isEqualTo(DATE_RANGE.checkOut());
      assertThat(message.ages()).containsExactly(30, 29, 1, 3);
   }
}
