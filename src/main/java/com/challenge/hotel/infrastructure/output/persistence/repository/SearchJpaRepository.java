package com.challenge.hotel.infrastructure.output.persistence.repository;

import com.challenge.hotel.infrastructure.output.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Spring Data JPA repository for SearchEntity persistence.
 * Provides database access through derived query methods.
 * Used exclusively by the persistence adapter — not exposed to the domain.
 */
public interface SearchJpaRepository extends JpaRepository<SearchEntity, Long> {

   /**
    * Finds a search entity by its business identifier.
    *
    * @param searchId the UUID search identifier
    * @return an Optional containing the entity if found
    */
   Optional<SearchEntity> findBySearchId(String searchId);

   /**
    * Counts searches equal to the given parameters.
    * Age order is significant — "30,29,1,3" differs from "3,1,29,30".
    *
    * @param hotelId  the hotel identifier
    * @param checkIn  the check-in date
    * @param checkOut the check-out date
    * @param ages     the serialized ages string preserving insertion order
    * @return the count of matching searches
    */
   long countByHotelIdAndCheckInAndCheckOutAndAges(
         String hotelId,
         LocalDate checkIn,
         LocalDate checkOut,
         String ages);
}