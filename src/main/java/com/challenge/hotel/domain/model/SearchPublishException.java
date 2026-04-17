package com.challenge.hotel.domain.model;

/**
 * Domain exception thrown when a search event cannot be published to the event stream.
 * Indicates an infrastructure failure — the Kafka broker is unavailable or the publish timed out.
 * Maps to HTTP 503 Service Unavailable in the REST layer.
 */
public class SearchPublishException extends RuntimeException {

   public SearchPublishException(final String searchId, final Throwable cause) {
      super("Failed to publish search event for searchId: %s".formatted(searchId), cause);
   }
}
