package es.onebox.mgmt.datasources.ms.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ConditionsData implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("operator_id")
    private Integer operatorId;

    @JsonProperty("entity_id")
    private Integer entityId;

    @JsonProperty("client_entity_id")
    private Integer clientEntityId;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("event_id")
    private Integer eventId;

    @JsonProperty("condition_group_type")
    private ConditionGroupType conditionGroupType;

    @JsonProperty("condition_group_id")
    private Integer conditionGroupId;

    private List<ConditionData<?>> conditions;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Integer clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public ConditionGroupType getConditionGroupType() {
        return conditionGroupType;
    }

    public void setConditionGroupType(ConditionGroupType conditionGroupType) {
        this.conditionGroupType = conditionGroupType;
    }

    public Integer getConditionGroupId() {
        return conditionGroupId;
    }

    public void setConditionGroupId(Integer conditionGroupId) {
        this.conditionGroupId = conditionGroupId;
    }

    public List<ConditionData<?>> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionData<?>> conditions) {
        this.conditions = conditions;
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
