package es.onebox.common.datasources.webhook.dto.fever.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.DateUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class FeverKafkaPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 4456214312971792147L;

    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("event_fqn")
    private String eventFqn;
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("payload")
    private String payload;

    public FeverKafkaPayload() {
    }

    public FeverKafkaPayload(String eventFqn, String eventId, String payload) {
        this.createdAt = ZonedDateTime.now(ZoneOffset.UTC).format(DateUtils.FORMATTER);
        this.eventFqn = eventFqn;
        this.eventId = eventId;
        this.payload = payload;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEventFqn() {
        return eventFqn;
    }

    public void setEventFqn(String eventFqn) {
        this.eventFqn = eventFqn;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
