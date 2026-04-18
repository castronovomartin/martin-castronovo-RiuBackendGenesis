package com.challenge.hotel.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Unit tests for Search domain entity.
 * Verifies aggregate root behavior, factory method and null validation.
 */
class SearchTest {

   private static final HotelId HOTEL_ID = new HotelId("1234aBc");
   private static final DateRange DATE_RANGE = new DateRange(
         LocalDate.of(2023, 12, 29),
         LocalDate.of(2023, 12, 31)
   );
   private static final Ages AGES = new Ages(List.of(30, 29, 1, 3));

   @Test
   @DisplayName("Should create Search with generated SearchId via factory method")
   void shouldCreateSearchWithGeneratedSearchId() {
      final var search = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      assertAll(
            () -> assertThat(search.searchId()).isNotNull(),
            () -> assertThat(search.searchId().value()).isNotBlank(),
            () -> assertThat(search.hotelId()).isEqualTo(HOTEL_ID),
            () -> assertThat(search.dateRange()).isEqualTo(DATE_RANGE),
            () -> assertThat(search.ages()).isEqualTo(AGES)
      );
   }

   @Test
   @DisplayName("Should generate unique SearchId for each created Search")
   void shouldGenerateUniqueSearchIdForEachSearch() {
      final var first = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var second = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      assertThat(first.searchId()).isNotEqualTo(second.searchId());
   }

   @Test
   @DisplayName("Should consider two searches with same data but different SearchId as not equal")
   void shouldConsiderSearchesWithDifferentSearchIdAsNotEqual() {
      final var first = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      final var second = Search.create(HOTEL_ID, DATE_RANGE, AGES);
      assertThat(first).isNotEqualTo(second);
   }

   @Test
   @DisplayName("Should throw exception when hotelId is null")
   void shouldThrowExceptionWhenHotelIdIsNull() {
      assertThatThrownBy(() -> Search.create(null, DATE_RANGE, AGES))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("HotelId must not be null");
   }

   @Test
   @DisplayName("Should throw exception when dateRange is null")
   void shouldThrowExceptionWhenDateRangeIsNull() {
      assertThatThrownBy(() -> Search.create(HOTEL_ID, null, AGES))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("DateRange must not be null");
   }

   @Test
   @DisplayName("Should throw exception when ages is null")
   void shouldThrowExceptionWhenAgesIsNull() {
      assertThatThrownBy(() -> Search.create(HOTEL_ID, DATE_RANGE, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ages must not be null");
   }

   @Test
   @DisplayName("Should create Search with existing SearchId via constructor")
   void shouldCreateSearchWithExistingSearchId() {
      final var searchId = SearchId.generate();
      final var search = new Search(searchId, HOTEL_ID, DATE_RANGE, AGES);
      assertThat(search.searchId()).isEqualTo(searchId);
   }

   @Test
   @DisplayName("Should throw exception when searchId is null")
   void shouldThrowExceptionWhenSearchIdIsNull() {
      assertThatThrownBy(() -> new Search(null, HOTEL_ID, DATE_RANGE, AGES))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SearchId must not be null");
   }
}