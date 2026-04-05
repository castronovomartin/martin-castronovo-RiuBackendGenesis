package com.challenge.hotel.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Immutable Data Transfer Object representing the HTTP request body for POST /search.
 * Performs date parsing in the compact constructor — never in getters.
 * Custom validator {@link ValidDateRange} ensures checkIn is before checkOut.
 */
@ValidDateRange
public record SearchRequestDTO(

      @NotBlank(message = "hotelId must not be blank")
      String hotelId,

      @NotNull(message = "checkIn must not be null")
      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkIn,

      @NotNull(message = "checkOut must not be null")
      @JsonFormat(pattern = "dd/MM/yyyy")
      LocalDate checkOut,

      @NotEmpty(message = "ages must not be empty")
      List<Integer> ages

) {

   /**
    * Compact constructor — creates a defensive copy of the ages list
    * to prevent external mutation after construction.
    * Date parsing is handled by Jackson via @JsonFormat before this constructor runs.
    */
   public SearchRequestDTO {
      if (ages != null) {
         ages = List.copyOf(ages);
      }
   }
}
