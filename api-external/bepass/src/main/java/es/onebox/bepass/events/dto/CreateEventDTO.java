package es.onebox.bepass.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record CreateEventDTO(@JsonProperty("session_id") @NotNull Long sessionId,
                             @JsonProperty("location_id") @NotNull String locationId)
        implements Serializable {
}
