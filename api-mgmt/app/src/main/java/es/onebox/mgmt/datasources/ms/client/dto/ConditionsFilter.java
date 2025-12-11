package es.onebox.mgmt.datasources.ms.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SortableElement;
import es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConditionsFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    @JsonProperty("event_id")
    private Long eventId;
    @JsonProperty("entity_id")
    private Long entityId;
    @JsonProperty("operator_id")
    private Long operatorId;
    @JsonProperty("client_entity_id")
    private Long clientEntityId;
    @JsonProperty("condition_group_type")
    private ConditionGroupType conditionGroupType;
    private String q;
    private SortableElement sortBy;
    private Boolean sortAsc;

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

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Long clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public ConditionGroupType getConditionGroupType() {
        return conditionGroupType;
    }

    public void setConditionGroupType(ConditionGroupType conditionGroupType) {
        this.conditionGroupType = conditionGroupType;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortableElement getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortableElement sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(Boolean sortAsc) {
        this.sortAsc = sortAsc;
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
