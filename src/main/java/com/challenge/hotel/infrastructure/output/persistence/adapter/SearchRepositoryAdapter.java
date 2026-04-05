package com.challenge.hotel.infrastructure.output.persistence.adapter;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.port.output.SearchRepository;
import com.challenge.hotel.infrastructure.output.persistence.entity.SearchEntity;
import com.challenge.hotel.infrastructure.output.persistence.repository.SearchJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing the SearchRepository output port.
 * Translates between domain objects and JPA entities.
 * Handles ages serialization — domain uses List<Integer>, database uses String.
 */
@Component
public class SearchRepositoryAdapter implements SearchRepository {

   private static final String AGES_DELIMITER = ",";

   private final SearchJpaRepository searchJpaRepository;

   /**
    * Constructor injection — ensures adapter is always in a valid state.
    *
    * @param searchJpaRepository the Spring Data JPA repository
    */
   public SearchRepositoryAdapter(final SearchJpaRepository searchJpaRepository) {
      this.searchJpaRepository = searchJpaRepository;
   }

   /**
    * Persists a domain Search object to Oracle database.
    * Converts domain value objects to primitive types for JPA persistence.
    *
    * @param search the domain search object to persist
    */
   @Override
   public void save(final Search search) {
      final var entity = toEntity(search);
      searchJpaRepository.save(entity);
   }

   /**
    * Finds a domain Search object by its unique identifier.
    *
    * @param searchId the unique search identifier
    * @return an Optional containing the domain Search if found
    */
   @Override
   public Optional<Search> findById(final SearchId searchId) {
      return searchJpaRepository
            .findBySearchId(searchId.value())
            .map(this::toDomain);
   }

   /**
    * Counts searches equal to the given search.
    * Age order is significant — serialized String preserves insertion order.
    *
    * @param search the reference search to compare against
    * @return the count of equal searches
    */
   @Override
   public long countEquals(final Search search) {
      return searchJpaRepository.countByHotelIdAndCheckInAndCheckOutAndAges(
            search.hotelId().value(),
            search.dateRange().checkIn(),
            search.dateRange().checkOut(),
            serializeAges(search.ages())
      );
   }

   /**
    * Converts a domain Search object to a JPA SearchEntity.
    * Ages are serialized to a comma-separated String preserving order.
    *
    * @param search the domain search object
    * @return the JPA entity ready for persistence
    */
   private SearchEntity toEntity(final Search search) {
      return new SearchEntity(
            search.searchId().value(),
            search.hotelId().value(),
            search.dateRange().checkIn(),
            search.dateRange().checkOut(),
            serializeAges(search.ages())
      );
   }

   /**
    * Converts a JPA SearchEntity to a domain Search object.
    * Ages are deserialized from comma-separated String to List<Integer>.
    *
    * @param entity the JPA entity from database
    * @return the domain Search object
    */
   private Search toDomain(final SearchEntity entity) {
      return new Search(
            new SearchId(entity.getSearchId()),
            new HotelId(entity.getHotelId()),
            new DateRange(entity.getCheckIn(), entity.getCheckOut()),
            new Ages(deserializeAges(entity.getAges()))
      );
   }

   /**
    * Serializes an Ages value object to a comma-separated String.
    * Preserves insertion order — "30,29,1,3" differs from "3,1,29,30".
    *
    * @param ages the Ages value object
    * @return comma-separated String representation
    */
   private String serializeAges(final Ages ages) {
      return String.join(
            AGES_DELIMITER,
            ages.values().stream()
                .map(String::valueOf)
                .toList()
      );
   }

   /**
    * Deserializes a comma-separated String to a List of integers.
    * Preserves insertion order for correct count matching.
    *
    * @param ages the comma-separated ages String from database
    * @return ordered List of integer ages
    */
   private List<Integer> deserializeAges(final String ages) {
      return Arrays.stream(ages.split(AGES_DELIMITER))
                   .map(Integer::parseInt)
                   .toList();
   }
}
