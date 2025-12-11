package es.onebox.mgmt.datasources.ms.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class DeleteConditionsFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("client_entities_ids")
    private List<Long> clientEntitiesIds;
    @JsonProperty("condition_group_type")
    private ConditionGroupType conditionGroupType;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public List<Long> getClientEntitiesIds() {
        return clientEntitiesIds;
    }

    public void setClientEntitiesIds(List<Long> clientEntitiesIds) {
        this.clientEntitiesIds = clientEntitiesIds;
    }

    public ConditionGroupType getConditionGroupType() {
        return conditionGroupType;
    }

    public void setConditionGroupType(ConditionGroupType conditionGroupType) {
        this.conditionGroupType = conditionGroupType;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
