package es.onebox.event.datasources.ms.client.dto.conditions.generic;

import es.onebox.event.datasources.ms.client.dto.conditions.ConditionDTO;

import java.util.List;

public abstract class MultiValueCondition<T> extends ConditionDTO<List<T>> {
    public MultiValueCondition() {
    }

    public MultiValueCondition(List<T> values) {
        this.value = values;
    }
}
