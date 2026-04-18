package com.challenge.hotel.infrastructure.input.rest;

import com.challenge.hotel.domain.model.SearchId;
import com.challenge.hotel.application.port.input.SearchUseCase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for SearchController.
 * Uses @WebMvcTest to load only the web layer — no Kafka or Oracle needed.
 * Verifies HTTP contract: request validation, response format and status codes.
 */
@WebMvcTest(SearchController.class)
class SearchControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private SearchUseCase searchUseCase;

   @Test
   @DisplayName("Should return 201 with searchId when request is valid")
   void shouldReturn201WithSearchIdWhenRequestIsValid() throws Exception {
      when(searchUseCase.search(any())).thenReturn(new SearchId("test-uuid-123"));
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                                {
                                    "hotelId": "1234aBc",
                                    "checkIn": "29/12/2023",
                                    "checkOut": "31/12/2023",
                                    "ages": [30, 29, 1, 3]
                                }
                                """))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$.searchId").value("test-uuid-123"));
   }

   @Test
   @DisplayName("Should return 400 when hotelId is blank")
   void shouldReturn400WhenHotelIdIsBlank() throws Exception {
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                                {
                                    "hotelId": "",
                                    "checkIn": "29/12/2023",
                                    "checkOut": "31/12/2023",
                                    "ages": [30, 29, 1, 3]
                                }
                                """))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return 400 when checkIn is after checkOut")
   void shouldReturn400WhenCheckInIsAfterCheckOut() throws Exception {
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                                {
                                    "hotelId": "1234aBc",
                                    "checkIn": "31/12/2023",
                                    "checkOut": "29/12/2023",
                                    "ages": [30, 29, 1, 3]
                                }
                                """))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return 400 when ages is empty")
   void shouldReturn400WhenAgesIsEmpty() throws Exception {
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                                {
                                    "hotelId": "1234aBc",
                                    "checkIn": "29/12/2023",
                                    "checkOut": "31/12/2023",
                                    "ages": []
                                }
                                """))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return 400 when checkIn equals checkOut")
   void shouldReturn400WhenCheckInEqualsCheckOut() throws Exception {
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                                {
                                    "hotelId": "1234aBc",
                                    "checkIn": "29/12/2023",
                                    "checkOut": "29/12/2023",
                                    "ages": [30, 29, 1, 3]
                                }
                                """))
             .andExpect(status().isBadRequest());
   }

   @Test
   @DisplayName("Should return 400 when request body is missing")
   void shouldReturn400WhenRequestBodyIsMissing() throws Exception {
      mockMvc.perform(post("/search")
                   .contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isBadRequest());
   }
}
