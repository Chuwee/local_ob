package es.onebox.mgmt.common.conditions.conditiontype;

import es.onebox.mgmt.common.conditions.Condition;

import java.util.List;

abstract class McCondition<T> extends Condition<List<T>> {
    public McCondition() {
    }

    public McCondition(List<T> values) {
        this.value = values;
    }
}
