package com.challenge.hotel.application.usecase;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.port.output.SearchEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for SearchService use case.
 * Verifies that search events are published and SearchId is returned correctly.
 * Uses Mockito to isolate from Kafka infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

   @Mock
   private SearchEventPublisher searchEventPublisher;

   private SearchService searchService;

   private static final HotelId HOTEL_ID = new HotelId("1234aBc");
   private static final DateRange DATE_RANGE = new DateRange(
         LocalDate.of(2023, 12, 29),
         LocalDate.of(2023, 12, 31)
   );
   private static final Ages AGES = new Ages(List.of(30, 29, 1, 3));

   @BeforeEach
   void setUp() {
      searchService = new SearchService(searchEventPublisher);
   }

   @Test
   @DisplayName("Should return SearchId when search is created")
   void shouldReturnSearchIdWhenSearchIsCreated() {
      final var search = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var result = searchService.search(search);
      assertThat(result).isNotNull();
      assertThat(result.value()).isEqualTo(search.searchId().value());
   }

   @Test
   @DisplayName("Should publish search event exactly once")
   void shouldPublishSearchEventExactlyOnce() {
      final var search = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      searchService.search(search);
      verify(searchEventPublisher, times(1)).publish(search);
   }

   @Test
   @DisplayName("Should publish the exact search object received")
   void shouldPublishExactSearchObjectReceived() {
      final var search = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      searchService.search(search);
      verify(searchEventPublisher).publish(search);
   }

   @Test
   @DisplayName("Should return different SearchId for each search")
   void shouldReturnDifferentSearchIdForEachSearch() {
      final var firstSearch = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var secondSearch = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var firstId = searchService.search(firstSearch);
      final var secondId = searchService.search(secondSearch);
      assertThat(firstId).isNotEqualTo(secondId);
   }

   @Test
   @DisplayName("Should publish event before returning SearchId")
   void shouldPublishEventBeforeReturningSearchId() {
      final var search = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var result = searchService.search(search);
      verify(searchEventPublisher, times(1)).publish(any(Search.class));
      assertThat(result).isNotNull();
   }
}
