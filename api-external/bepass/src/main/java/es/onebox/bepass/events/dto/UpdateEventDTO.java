package es.onebox.bepass.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;

public record UpdateEventDTO(Boolean active,
                             @JsonProperty("location_id") String locationId,
                             @JsonProperty("event_name") String eventName,
                             ZonedDateTime start,
                             ZonedDateTime end ) implements Serializable {
}
