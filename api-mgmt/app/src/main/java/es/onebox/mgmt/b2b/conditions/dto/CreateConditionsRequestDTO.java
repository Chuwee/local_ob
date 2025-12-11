package es.onebox.mgmt.b2b.conditions.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class CreateConditionsRequestDTO extends IdDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "conditions can not be null")
    @Valid
    private List<CreateConditionDTO<?>> conditions;
    private ConditionManager conditionManager;

    public CreateConditionsRequestDTO(){}

    public CreateConditionsRequestDTO(List<CreateConditionDTO<?>> conditions) {
        this.conditions = conditions;
        this.conditionManager = new ConditionManager(conditions);
    }

    public List<CreateConditionDTO<?>> getConditions() {
        return conditionManager.getConditions();
    }

    public void setConditions(List<CreateConditionDTO<?>> conditions) {
        this.conditions = conditions;
        this.conditionManager = new ConditionManager(conditions);
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