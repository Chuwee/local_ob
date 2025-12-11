package es.onebox.mgmt.forms.enums;

import java.io.Serializable;
import java.util.List;

public enum ValidationRuleType implements Serializable {

    MIN_YEAR(List.of(FormFieldType.DATE), true, ValidationRuleValueType.INTEGER),
    MAX_YEAR(List.of(FormFieldType.DATE), true, ValidationRuleValueType.INTEGER),
    REQUIRED_WHEN_AGE_IS_BIGGER_THAN(List.of(FormFieldType.TEXT, FormFieldType.NUMBER), true, ValidationRuleValueType.INTEGER),
    REGEX(List.of(FormFieldType.TEXT, FormFieldType.NUMBER, FormFieldType.DATE, FormFieldType.EMAIL),
            true, ValidationRuleValueType.REGEX);

    private final List<FormFieldType> types;
    private final Boolean valueRequired;
    private final ValidationRuleValueType valueType;

    ValidationRuleType(List<FormFieldType> types, Boolean valueRequired, ValidationRuleValueType valueType) {
        this.types = types;
        this.valueRequired = valueRequired;
        this.valueType = valueType;
    }

    public List<FormFieldType> getTypes() {
        return types;
    }

    public Boolean getValueRequired() {
        return valueRequired;
    }

    public ValidationRuleValueType getValueType() {
        return valueType;
    }

    public Boolean containsType(String compType) {
        return types.stream().anyMatch(type -> type.toString().equals(compType));
    }

    public static ValidationRuleType get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(types);
    }
}
