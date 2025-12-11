package es.onebox.event.forms.domain;

import java.util.List;

public enum ValidationRuleType {

    MIN_YEAR(List.of(FieldType.DATE)),
    MAX_YEAR(List.of(FieldType.DATE)),
    REGEX(List.of(FieldType.TEXT, FieldType.NUMBER, FieldType.DATE, FieldType.EMAIL));

    private final List<FieldType> types;

    ValidationRuleType(List<FieldType> types) {
        this.types = types;
    }

    public Boolean containsType(FieldType type) {
        return types.contains(type);
    }
} 