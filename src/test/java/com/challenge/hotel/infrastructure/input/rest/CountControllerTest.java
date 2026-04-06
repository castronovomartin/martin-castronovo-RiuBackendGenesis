package com.challenge.hotel.infrastructure.input.rest;

import com.challenge.hotel.domain.model.Ages;
import com.challenge.hotel.domain.model.DateRange;
import com.challenge.hotel.domain.model.HotelId;
import com.challenge.hotel.domain.model.Search;
import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.domain.port.input.CountUseCase;
import com.challenge.hotel.domain.port.input.CountUseCase.CountResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for CountController.
 * Uses @WebMvcTest to load only the web layer — no Kafka or Oracle needed.
 * Verifies HTTP contract: request validation, response format and status codes.
 */
@WebMvcTest(CountController.class)
class CountControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private CountUseCase countUseCase;

   private static final SearchId SEARCH_ID = new SearchId("test-uuid-123");
   private static final HotelId HOTEL_ID = new HotelId("1234aBc");
   private static final DateRange DATE_RANGE = new DateRange(
         LocalDate.of(2023, 12, 29),
         LocalDate.of(2023, 12, 31)
   );
   private static final Ages AGES = new Ages(List.of(30, 29, 1, 3));
   private static final Search SEARCH = new Search(SEARCH_ID, HOTEL_ID, DATE_RANGE, AGES);

   @Test
   @DisplayName("Should return 200 with count result when searchId is valid")
   void shouldReturn200WithCountResultWhenSearchIdIsValid() throws Exception {
      when(countUseCase.count(any())).thenReturn(new CountResult(SEARCH, 100L));
      mockMvc.perform(get("/count")
                   .param("searchId", SEARCH_ID.value()))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.searchId").value(SEARCH_ID.value()))
             .andExpect(jsonPath("$.search.hotelId").value(HOTEL_ID.value()))
             .andExpect(jsonPath("$.search.checkIn").value("29/12/2023"))
             .andExpect(jsonPath("$.search.checkOut").value("31/12/2023"))
             .andExpect(jsonPath("$.search.ages[0]").value(30))
             .andExpect(jsonPath("$.search.ages[1]").value(29))
             .andExpect(jsonPath("$.count").value(100));
   }

   @Test
   @DisplayName("Should return 400 when searchId is blank")
   void shouldReturn400WhenSearchIdIsBlank() throws Exception {
      mockMvc.perform(get("/count")
                   .param("searchId", ""))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return 400 when searchId param is missing")
   void shouldReturn400WhenSearchIdParamIsMissing() throws Exception {
      mockMvc.perform(get("/count"))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return correct age order in response")
   void shouldReturnCorrectAgeOrderInResponse() throws Exception {
      final var agesReversed = new Ages(List.of(3, 1, 29, 30));
      final var searchReversed = new Search(SEARCH_ID, HOTEL_ID, DATE_RANGE, agesReversed);
      when(countUseCase.count(any())).thenReturn(new CountResult(searchReversed, 50L));
      mockMvc.perform(get("/count")
                   .param("searchId", SEARCH_ID.value()))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.search.ages[0]").value(3))
             .andExpect(jsonPath("$.search.ages[1]").value(1))
             .andExpect(jsonPath("$.search.ages[2]").value(29))
             .andExpect(jsonPath("$.search.ages[3]").value(30));
   }
}
