package es.onebox.mgmt.b2b.conditions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ClientConditionsFilterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("season_ticket_id")
    private Long seasonTicketId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("entity_id")
    private Long entityId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
