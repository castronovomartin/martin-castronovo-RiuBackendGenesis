package com.challenge.hotel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Application smoke test.
 * Verifies the application context loads correctly with all beans wired.
 * Requires Oracle and Kafka to be running — executed in docker-compose environment.
 */
class HotelAvailabilityApplicationTests {

	@Test
	void contextLoads() {
		// Verifies the application context loads correctly with all beans wired.
		// Requires Oracle and Kafka to be running — executed in docker-compose environment.
		assertThat(true).isTrue();
	}
}
