package com.challenge.hotel.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Ages value object.
 * Verifies immutability, defensive copy, order preservation and validation.
 */
class AgesTest {

   @Test
   @DisplayName("Should create Ages with valid list")
   void shouldCreateAgesWithValidList() {
      final var ages = new Ages(List.of(30, 29, 1, 3));
      assertThat(ages.values()).containsExactly(30, 29, 1, 3);
   }

   @Test
   @DisplayName("Should preserve insertion order")
   void shouldPreserveInsertionOrder() {
      final var ages = new Ages(List.of(30, 29, 1, 3));
      assertThat(ages.values()).containsExactly(30, 29, 1, 3);
      assertThat(ages.values()).doesNotContainSequence(3, 1, 29, 30);
   }

   @Test
   @DisplayName("Should be immutable after creation — defensive copy prevents external mutation")
   void shouldBeImmutableAfterCreation() {
      final var mutableList = new ArrayList<>(List.of(30, 29, 1, 3));
      final var ages = new Ages(mutableList);
      mutableList.add(99);
      assertThat(ages.values()).containsExactly(30, 29, 1, 3);
      assertThat(ages.values()).hasSize(4);
   }

   @Test
   @DisplayName("Should throw exception when modifying internal list")
   void shouldThrowExceptionWhenModifyingInternalList() {
      final var ages = new Ages(List.of(30, 29, 1, 3));
      assertThatThrownBy(() -> ages.values().add(99))
            .isInstanceOf(UnsupportedOperationException.class);
   }

   @Test
   @DisplayName("Should throw exception when list is null")
   void shouldThrowExceptionWhenListIsNull() {
      assertThatThrownBy(() -> new Ages(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ages list must not be null or empty");
   }

   @Test
   @DisplayName("Should throw exception when list is empty")
   void shouldThrowExceptionWhenListIsEmpty() {
      assertThatThrownBy(() -> new Ages(List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Ages list must not be null or empty");
   }

   @Test
   @DisplayName("Should not be equal when same ages in different order")
   void shouldNotBeEqualWhenSameAgesInDifferentOrder() {
      final var first = new Ages(List.of(30, 29, 1, 3));
      final var second = new Ages(List.of(3, 1, 29, 30));
      assertThat(first).isNotEqualTo(second);
   }

   @Test
   @DisplayName("Should be equal when same ages in same order")
   void shouldBeEqualWhenSameAgesInSameOrder() {
      final var first = new Ages(List.of(30, 29, 1, 3));
      final var second = new Ages(List.of(30, 29, 1, 3));
      assertThat(first).isEqualTo(second);
   }
}
