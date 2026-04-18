package com.challenge.hotel.infrastructure.input.rest;

import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.application.port.input.CountUseCase;
import com.challenge.hotel.infrastructure.input.rest.dto.CountResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST input adapter for hotel availability search count.
 * Retrieves the reference search and returns the count of equal searches.
 */
@RestController
@RequestMapping("/count")
@Tag(name = "Hotel Availability", description = "Hotel availability search operations")
public class CountController {

   private final CountUseCase countUseCase;

   /**
    * Constructor injection — ensures controller is always in a valid state.
    *
    * @param countUseCase the input port for search counting
    */
   public CountController(final CountUseCase countUseCase) {
      this.countUseCase = countUseCase;
   }

   /**
    * Returns the count of searches equal to the one identified by searchId.
    * Age order is significant — searches with same ages in different order are not equal.
    * Validates searchId manually to avoid Spring AOP proxy wrapping domain exceptions.
    *
    * @param searchId the identifier of the reference search
    * @return HTTP 200 with search details and count of equal searches
    * @throws IllegalArgumentException if searchId is blank
    */
   @GetMapping
   @Operation(
         summary = "Count equal hotel availability searches",
         description = "Returns how many searches are equal to the reference search. Age order is significant."
   )
   public ResponseEntity<CountResponseDTO> count(
         @Parameter(description = "The search identifier returned by POST /search")
         @RequestParam final String searchId) {
      if (searchId == null || searchId.isBlank()) {
         throw new IllegalArgumentException("searchId must not be blank");
      }
      final var result = countUseCase.count(new SearchId(searchId));
      return ResponseEntity.ok(CountResponseDTO.from(searchId, result));
   }
}