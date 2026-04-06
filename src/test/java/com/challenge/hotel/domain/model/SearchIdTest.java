package com.challenge.hotel.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for SearchId value object.
 * Verifies immutability, validation and unique generation behavior.
 */
class SearchIdTest {

   @Test
   @DisplayName("Should create SearchId with valid value")
   void shouldCreateSearchIdWithValidValue() {
      final var searchId = new SearchId("valid-uuid-value");
      assertThat(searchId.value()).isEqualTo("valid-uuid-value");
   }

   @Test
   @DisplayName("Should generate unique SearchId without database access")
   void shouldGenerateUniqueSearchId() {
      final var first = SearchId.generate();
      final var second = SearchId.generate();
      assertThat(first).isNotEqualTo(second);
      assertThat(first.value()).isNotBlank();
      assertThat(second.value()).isNotBlank();
   }

   @Test
   @DisplayName("Should throw exception when value is null")
   void shouldThrowExceptionWhenValueIsNull() {
      assertThatThrownBy(() -> new SearchId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SearchId value must not be null or blank");
   }

   @Test
   @DisplayName("Should throw exception when value is blank")
   void shouldThrowExceptionWhenValueIsBlank() {
      assertThatThrownBy(() -> new SearchId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SearchId value must not be null or blank");
   }

   @Test
   @DisplayName("Should be equal when values are equal")
   void shouldBeEqualWhenValuesAreEqual() {
      final var first = new SearchId("same-value");
      final var second = new SearchId("same-value");
      assertThat(first).isEqualTo(second);
      assertThat(first.hashCode()).isEqualTo(second.hashCode());
   }
}
