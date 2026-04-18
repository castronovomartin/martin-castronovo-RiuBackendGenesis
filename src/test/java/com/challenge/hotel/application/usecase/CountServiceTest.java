package com.challenge.hotel.application.usecase;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.model.SearchNotFoundException;
import com.challenge.hotel.application.port.input.CountUseCase.CountResult;
import com.challenge.hotel.domain.port.output.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CountService use case.
 * Verifies search retrieval, count logic and error handling.
 * Uses Mockito to isolate from Oracle infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class CountServiceTest {

   @Mock
   private SearchRepository searchRepository;

   private CountService countService;

   private static final SearchId SEARCH_ID = SearchId.generate();
   private static final HotelId HOTEL_ID = new HotelId("1234aBc");
   private static final DateRange DATE_RANGE = new DateRange(
         LocalDate.of(2023, 12, 29),
         LocalDate.of(2023, 12, 31)
   );
   private static final Ages AGES = new Ages(List.of(30, 29, 1, 3));
   private static final Search SEARCH = new Search(SEARCH_ID, HOTEL_ID, DATE_RANGE, AGES);

   @BeforeEach
   void setUp() {
      countService = new CountService(searchRepository);
   }

   @Test
   @DisplayName("Should return count result with search and count")
   void shouldReturnCountResultWithSearchAndCount() {
      when(searchRepository.findById(SEARCH_ID)).thenReturn(Optional.of(SEARCH));
      when(searchRepository.countEquals(SEARCH)).thenReturn(100L);
      final var result = countService.count(SEARCH_ID);
      assertThat(result.search()).isEqualTo(SEARCH);
      assertThat(result.count()).isEqualTo(100L);
   }

   @Test
   @DisplayName("Should throw exception when searchId not found")
   void shouldThrowExceptionWhenSearchIdNotFound() {
      when(searchRepository.findById(SEARCH_ID)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> countService.count(SEARCH_ID))
            .isInstanceOf(SearchNotFoundException.class)
            .hasMessageContaining("No search found for searchId");
   }

   @Test
   @DisplayName("Should return count of one when only one matching search exists")
   void shouldReturnCountOfOneWhenOnlyOneMatchingSearchExists() {
      when(searchRepository.findById(SEARCH_ID)).thenReturn(Optional.of(SEARCH));
      when(searchRepository.countEquals(SEARCH)).thenReturn(1L);
      final var result = countService.count(SEARCH_ID);
      assertThat(result.count()).isEqualTo(1L);
   }

   @Test
   @DisplayName("Should return correct search details in result")
   void shouldReturnCorrectSearchDetailsInResult() {
      when(searchRepository.findById(SEARCH_ID)).thenReturn(Optional.of(SEARCH));
      when(searchRepository.countEquals(SEARCH)).thenReturn(50L);
      final CountResult result = countService.count(SEARCH_ID);
      assertThat(result.search().hotelId()).isEqualTo(HOTEL_ID);
      assertThat(result.search().dateRange()).isEqualTo(DATE_RANGE);
      assertThat(result.search().ages()).isEqualTo(AGES);
   }
}
