package es.onebox.mgmt.b2b.conditions.dto;

import java.util.List;

public class ConditionManager implements ConditionsRequest {

    private List<CreateConditionDTO<?>> conditions;

    public ConditionManager(List<CreateConditionDTO<?>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public List<CreateConditionDTO<?>> getConditions() {
        return conditions;
    }

}
