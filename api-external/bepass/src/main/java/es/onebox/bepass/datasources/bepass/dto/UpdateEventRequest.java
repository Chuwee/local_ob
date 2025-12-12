package es.onebox.bepass.datasources.bepass.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UpdateEventRequest(Boolean isActive, String locationId,
                                 String eventName,
                                 LocalDateTime startDateTime,
                                 LocalDateTime endDateTime) implements Serializable {
}
