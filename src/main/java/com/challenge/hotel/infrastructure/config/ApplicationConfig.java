package com.challenge.hotel.infrastructure.config;

import com.challenge.hotel.application.usecase.CountService;
import com.challenge.hotel.application.usecase.SearchService;
import com.challenge.hotel.domain.port.input.CountUseCase;
import com.challenge.hotel.domain.port.input.SearchUseCase;
import com.challenge.hotel.domain.port.output.SearchEventPublisher;
import com.challenge.hotel.domain.port.output.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that wires all application layer beans.
 * Instantiates use cases manually via constructor injection —
 * keeping the application layer free of Spring annotations.
 */
@Configuration
public class ApplicationConfig {

   /**
    * Registers SearchService as the SearchUseCase bean.
    * Spring injects SearchKafkaPublisher as the SearchEventPublisher implementation.
    *
    * @param searchEventPublisher the Kafka publisher implementation
    * @return the SearchUseCase bean
    */
   @Bean
   public SearchUseCase searchUseCase(final SearchEventPublisher searchEventPublisher) {
      return new SearchService(searchEventPublisher);
   }

   /**
    * Registers CountService as the CountUseCase bean.
    * Spring injects SearchRepositoryAdapter as the SearchRepository implementation.
    *
    * @param searchRepository the JPA repository adapter implementation
    * @return the CountUseCase bean
    */
   @Bean
   public CountUseCase countUseCase(final SearchRepository searchRepository) {
      return new CountService(searchRepository);
   }
}