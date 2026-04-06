package com.challenge.hotel.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for HotelId value object.
 * Verifies immutability, validation and equality behavior.
 */
class HotelIdTest {

   @Test
   @DisplayName("Should create HotelId with valid value")
   void shouldCreateHotelIdWithValidValue() {
      final var hotelId = new HotelId("1234aBc");
      assertThat(hotelId.value()).isEqualTo("1234aBc");
   }

   @Test
   @DisplayName("Should throw exception when value is null")
   void shouldThrowExceptionWhenValueIsNull() {
      assertThatThrownBy(() -> new HotelId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("HotelId value must not be null or blank");
   }

   @Test
   @DisplayName("Should throw exception when value is blank")
   void shouldThrowExceptionWhenValueIsBlank() {
      assertThatThrownBy(() -> new HotelId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("HotelId value must not be null or blank");
   }

   @Test
   @DisplayName("Should throw exception when value is empty")
   void shouldThrowExceptionWhenValueIsEmpty() {
      assertThatThrownBy(() -> new HotelId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("HotelId value must not be null or blank");
   }

   @Test
   @DisplayName("Should be equal when values are equal")
   void shouldBeEqualWhenValuesAreEqual() {
      final var first = new HotelId("1234aBc");
      final var second = new HotelId("1234aBc");
      assertThat(first).isEqualTo(second);
      assertThat(first.hashCode()).isEqualTo(second.hashCode());
   }

   @Test
   @DisplayName("Should not be equal when values differ")
   void shouldNotBeEqualWhenValuesDiffer() {
      final var first = new HotelId("1234aBc");
      final var second = new HotelId("9999XYZ");
      assertThat(first).isNotEqualTo(second);
   }
}
