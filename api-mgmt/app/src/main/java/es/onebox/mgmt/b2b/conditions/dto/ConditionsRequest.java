package es.onebox.mgmt.b2b.conditions.dto;

import java.util.List;

public interface ConditionsRequest {
    List<CreateConditionDTO<?>> getConditions();
}
