package com.challenge.hotel.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * JPA entity representing a hotel availability search in Oracle database.
 * Intentionally separated from the domain Search object — persistence
 * concerns must not leak into the domain layer.
 */
@Entity
@Table(name = "searches")
public class SearchEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "search_id", nullable = false, unique = true, length = 36)
   private String searchId;

   @Column(name = "hotel_id", nullable = false, length = 100)
   private String hotelId;

   @Column(name = "check_in", nullable = false)
   private LocalDate checkin;

   @Column(name = "check_out", nullable = false)
   private LocalDate checkout;

   @Column(name = "ages", nullable = false, length = 255)
   private String ages;

   /**
    * Default constructor required by JPA.
    * Not intended for direct use — use the parameterized constructor instead.
    */
   protected SearchEntity() {}

   /**
    * Parameterized constructor for creating a new SearchEntity.
    *
    * @param searchId the unique business identifier (UUID)
    * @param hotelId  the hotel identifier
    * @param checkin  the check-in date
    * @param checkout the check-out date
    * @param ages     the serialized ages string (e.g. "30,29,1,3")
    */
   public SearchEntity(
         final String searchId,
         final String hotelId,
         final LocalDate checkin,
         final LocalDate checkout,
         final String ages) {
      this.searchId = searchId;
      this.hotelId = hotelId;
      this.checkin = checkin;
      this.checkout = checkout;
      this.ages = ages;
   }

   public String getSearchId() { return searchId; }
   public String getHotelId() { return hotelId; }
   public LocalDate getCheckin() { return checkin; }
   public LocalDate getCheckout() { return checkout; }
   public String getAges() { return ages; }
}
