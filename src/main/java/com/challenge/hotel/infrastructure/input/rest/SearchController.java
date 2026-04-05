package com.challenge.hotel.infrastructure.input.rest;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.port.input.SearchUseCase;
import com.challenge.hotel.infrastructure.input.rest.dto.SearchRequestDTO;
import com.challenge.hotel.infrastructure.input.rest.dto.SearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST input adapter for hotel availability search creation.
 * Translates HTTP requests to domain objects and delegates to the search use case.
 */
@RestController
@RequestMapping("/search")
@Tag(name = "Hotel Availability", description = "Hotel availability search operations")
public class SearchController {

   private final SearchUseCase searchUseCase;

   /**
    * Constructor injection — ensures controller is always in a valid state.
    *
    * @param searchUseCase the input port for search creation
    */
   public SearchController(final SearchUseCase searchUseCase) {
      this.searchUseCase = searchUseCase;
   }

   /**
    * Creates a new hotel availability search.
    * Validates the request, translates to domain objects and publishes to Kafka.
    *
    * @param request the immutable search request DTO
    * @return HTTP 201 with the generated search identifier
    */
   @PostMapping
   @Operation(
         summary = "Create a hotel availability search",
         description = "Validates the request, generates a unique searchId and publishes to Kafka"
   )
   public ResponseEntity<SearchResponseDTO> search(
         @Valid @RequestBody final SearchRequestDTO request) {
      final var search = Search.create(
            new HotelId(request.hotelId()),
            new DateRange(request.checkIn(), request.checkOut()),
            new Ages(request.ages())
      );
      final var searchId = searchUseCase.search(search);
      return ResponseEntity.status(HttpStatus.CREATED).body(SearchResponseDTO.from(searchId));
   }
}
