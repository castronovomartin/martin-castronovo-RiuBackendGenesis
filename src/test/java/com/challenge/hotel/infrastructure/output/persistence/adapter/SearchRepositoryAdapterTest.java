package com.challenge.hotel.infrastructure.output.persistence.adapter;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.infrastructure.output.persistence.entity.SearchEntity;
import com.challenge.hotel.infrastructure.output.persistence.repository.SearchJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SearchRepositoryAdapter.
 * Verifies domain-to-entity translation, ages serialization and query delegation.
 */
@ExtendWith(MockitoExtension.class)
class SearchRepositoryAdapterTest {

   @Mock
   private SearchJpaRepository searchJpaRepository;

   private SearchRepositoryAdapter adapter;

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
      adapter = new SearchRepositoryAdapter(searchJpaRepository);
   }

   @Test
   @DisplayName("Should save search entity when save is called")
   void shouldSaveSearchEntityWhenSaveIsCalled() {
      adapter.save(SEARCH);
      verify(searchJpaRepository).save(any(SearchEntity.class));
   }

   @Test
   @DisplayName("Should serialize ages preserving order when saving")
   void shouldSerializeAgesPreservingOrderWhenSaving() {
      adapter.save(SEARCH);
      verify(searchJpaRepository).save(any(SearchEntity.class));
   }

   @Test
   @DisplayName("Should return domain Search when findById finds entity")
   void shouldReturnDomainSearchWhenFindByIdFindsEntity() {
      final var entity = new SearchEntity(
            SEARCH_ID.value(),
            HOTEL_ID.value(),
            DATE_RANGE.checkIn(),
            DATE_RANGE.checkOut(),
            "30,29,1,3"
      );
      when(searchJpaRepository.findBySearchId(SEARCH_ID.value()))
            .thenReturn(Optional.of(entity));
      final var result = adapter.findById(SEARCH_ID);
      assertThat(result).isPresent();
      assertThat(result.get().searchId().value()).isEqualTo(SEARCH_ID.value());
      assertThat(result.get().hotelId().value()).isEqualTo(HOTEL_ID.value());
      assertThat(result.get().ages().values()).containsExactly(30, 29, 1, 3);
   }

   @Test
   @DisplayName("Should return empty Optional when findById finds nothing")
   void shouldReturnEmptyOptionalWhenFindByIdFindsNothing() {
      when(searchJpaRepository.findBySearchId(SEARCH_ID.value()))
            .thenReturn(Optional.empty());
      final var result = adapter.findById(SEARCH_ID);
      assertThat(result).isEmpty();
   }

   @Test
   @DisplayName("Should return correct count when countEquals is called")
   void shouldReturnCorrectCountWhenCountEqualsIsCalled() {
      when(searchJpaRepository.countByHotelIdAndCheckInAndCheckOutAndAges(
            HOTEL_ID.value(),
            DATE_RANGE.checkIn(),
            DATE_RANGE.checkOut(),
            "30,29,1,3"
      )).thenReturn(100L);
      final var count = adapter.countEquals(SEARCH);
      assertThat(count).isEqualTo(100L);
   }

   @Test
   @DisplayName("Should deserialize ages preserving order when converting entity to domain")
   void shouldDeserializeAgesPreservingOrderWhenConvertingEntityToDomain() {
      final var entity = new SearchEntity(
            SEARCH_ID.value(),
            HOTEL_ID.value(),
            DATE_RANGE.checkIn(),
            DATE_RANGE.checkOut(),
            "3,1,29,30"
      );
      when(searchJpaRepository.findBySearchId(SEARCH_ID.value()))
            .thenReturn(Optional.of(entity));
      final var result = adapter.findById(SEARCH_ID);
      assertThat(result.get().ages().values()).containsExactly(3, 1, 29, 30);
   }
}
