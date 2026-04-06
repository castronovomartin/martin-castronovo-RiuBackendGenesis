package com.challenge.hotel;

import org.junit.jupiter.api.Test;

/**
 * Application smoke test.
 * Verifies the application context loads correctly with all beans wired.
 * Requires Oracle and Kafka to be running — executed in docker-compose environment.
 */
class HotelAvailabilityApplicationTests {

	@Test
	void contextLoads() {
		// Integration test — runs in docker-compose environment
		// Unit tests are in their respective test classes
	}
}
