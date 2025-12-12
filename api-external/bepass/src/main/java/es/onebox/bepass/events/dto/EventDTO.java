package es.onebox.bepass.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;
// external_id : OB session id
public record EventDTO(String id, @JsonProperty("external_id") String externalId, String name, ZonedDateTime start, ZonedDateTime end, String status) implements Serializable {
}
