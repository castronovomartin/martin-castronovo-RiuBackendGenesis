package com.challenge.hotel.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for DateRange value object.
 * Verifies the core domain invariant: checkIn must be strictly before checkOut.
 */
class DateRangeTest {

   private static final LocalDate CHECK_IN = LocalDate.of(2023, 12, 29);
   private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);

   @Test
   @DisplayName("Should create DateRange with valid dates")
   void shouldCreateDateRangeWithValidDates() {
      final var dateRange = new DateRange(CHECK_IN, CHECK_OUT);
      assertThat(dateRange.checkIn()).isEqualTo(CHECK_IN);
      assertThat(dateRange.checkOut()).isEqualTo(CHECK_OUT);
   }

   @Test
   @DisplayName("Should throw exception when checkIn is null")
   void shouldThrowExceptionWhenCheckInIsNull() {
      assertThatThrownBy(() -> new DateRange(null, CHECK_OUT))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Check-in date must not be null");
   }

   @Test
   @DisplayName("Should throw exception when checkOut is null")
   void shouldThrowExceptionWhenCheckOutIsNull() {
      assertThatThrownBy(() -> new DateRange(CHECK_IN, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Check-out date must not be null");
   }

   @Test
   @DisplayName("Should throw exception when checkIn is after checkOut")
   void shouldThrowExceptionWhenCheckInIsAfterCheckOut() {
      assertThatThrownBy(() -> new DateRange(CHECK_OUT, CHECK_IN))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Check-in date must be strictly before check-out date");
   }

   @Test
   @DisplayName("Should throw exception when checkIn equals checkOut")
   void shouldThrowExceptionWhenCheckInEqualsCheckOut() {
      assertThatThrownBy(() -> new DateRange(CHECK_IN, CHECK_IN))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Check-in date must be strictly before check-out date");
   }

   @Test
   @DisplayName("Should be equal when dates are equal")
   void shouldBeEqualWhenDatesAreEqual() {
      final var first = new DateRange(CHECK_IN, CHECK_OUT);
      final var second = new DateRange(CHECK_IN, CHECK_OUT);
      assertThat(first).isEqualTo(second);
      assertThat(first.hashCode()).isEqualTo(second.hashCode());
   }
}
