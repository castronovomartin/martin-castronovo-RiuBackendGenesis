package com.challenge.hotel.infrastructure.input.kafka;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.port.output.SearchRepository;
import com.challenge.hotel.infrastructure.output.kafka.SearchMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for SearchKafkaConsumer input adapter.
 * Verifies that Kafka messages are correctly converted to domain objects and persisted.
 */
@ExtendWith(MockitoExtension.class)
class SearchKafkaConsumerTest {

   @Mock
   private SearchRepository searchRepository;

   private SearchKafkaConsumer consumer;

   private static final String SEARCH_ID_VALUE = "test-uuid-123";
   private static final String HOTEL_ID_VALUE = "1234aBc";
   private static final LocalDate CHECK_IN = LocalDate.of(2023, 12, 29);
   private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);
   private static final List<Integer> AGES_LIST = List.of(30, 29, 1, 3);

   @BeforeEach
   void setUp() {
      consumer = new SearchKafkaConsumer(searchRepository);
   }

   @Test
   @DisplayName("Should persist search when message is consumed")
   void shouldPersistSearchWhenMessageIsConsumed() {
      final var message = new SearchMessage(
            SEARCH_ID_VALUE,
            HOTEL_ID_VALUE,
            CHECK_IN,
            CHECK_OUT,
            AGES_LIST
      );
      consumer.consume(message);
      verify(searchRepository).save(org.mockito.ArgumentMatchers.any(Search.class));
   }

   @Test
   @DisplayName("Should convert message to domain object preserving all fields")
   void shouldConvertMessageToDomainObjectPreservingAllFields() {
      final var message = new SearchMessage(
            SEARCH_ID_VALUE,
            HOTEL_ID_VALUE,
            CHECK_IN,
            CHECK_OUT,
            AGES_LIST
      );
      final var searchCaptor = ArgumentCaptor.forClass(Search.class);
      consumer.consume(message);
      verify(searchRepository).save(searchCaptor.capture());
      final var search = searchCaptor.getValue();
      assertThat(search.searchId().value()).isEqualTo(SEARCH_ID_VALUE);
      assertThat(search.hotelId().value()).isEqualTo(HOTEL_ID_VALUE);
      assertThat(search.dateRange().checkIn()).isEqualTo(CHECK_IN);
      assertThat(search.dateRange().checkOut()).isEqualTo(CHECK_OUT);
      assertThat(search.ages().values()).containsExactly(30, 29, 1, 3);
   }

   @Test
   @DisplayName("Should use existing SearchId from message — not generate a new one")
   void shouldUseExistingSearchIdFromMessage() {
      final var message = new SearchMessage(
            SEARCH_ID_VALUE,
            HOTEL_ID_VALUE,
            CHECK_IN,
            CHECK_OUT,
            AGES_LIST
      );
      final var searchCaptor = ArgumentCaptor.forClass(Search.class);
      consumer.consume(message);
      verify(searchRepository).save(searchCaptor.capture());
      assertThat(searchCaptor.getValue().searchId().value()).isEqualTo(SEARCH_ID_VALUE);
   }

   @Test
   @DisplayName("Should preserve age order from message")
   void shouldPreserveAgeOrderFromMessage() {
      final var message = new SearchMessage(
            SEARCH_ID_VALUE,
            HOTEL_ID_VALUE,
            CHECK_IN,
            CHECK_OUT,
            List.of(3, 1, 29, 30)
      );
      final var searchCaptor = ArgumentCaptor.forClass(Search.class);
      consumer.consume(message);
      verify(searchRepository).save(searchCaptor.capture());
      assertThat(searchCaptor.getValue().ages().values()).containsExactly(3, 1, 29, 30);
   }
}
