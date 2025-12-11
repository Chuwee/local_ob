package es.onebox.event.datasources.ms.client.dto.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionsRequest {
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("client_entity_id")
    private Long clientEntityId;
    @JsonProperty("event_id")
    private Long eventId;

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

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Long clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

}
