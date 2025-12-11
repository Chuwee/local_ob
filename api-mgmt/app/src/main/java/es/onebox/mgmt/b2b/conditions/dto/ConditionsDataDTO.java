package es.onebox.mgmt.b2b.conditions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.conditions.enums.ConditionGroupType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ConditionsDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("condition_group_type")
    private ConditionGroupType conditionGroupType;

    @JsonProperty("conditions")
    private List<ConditionDTO> conditions;

    public ConditionGroupType getConditionGroupType() {
        return conditionGroupType;
    }

    public void setConditionGroupType(ConditionGroupType conditionGroupType) {
        this.conditionGroupType = conditionGroupType;
    }

    public List<ConditionDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionDTO> conditions) {
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
