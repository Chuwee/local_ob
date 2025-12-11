package es.onebox.mgmt.channels.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.forms.enums.DynamicFormValidationRule;
import es.onebox.mgmt.channels.forms.enums.ValidationRuleValueType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DynamicFormValidatorInfoDTO {

    private DynamicFormValidationRule rule;
    @JsonProperty("value_required")
    private Boolean valueRequired;
    @JsonProperty("value_type")
    private ValidationRuleValueType valueType;

    public DynamicFormValidationRule getRule() {
        return rule;
    }

    public void setRule(DynamicFormValidationRule rule) {
        this.rule = rule;
    }

    public Boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(Boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    public ValidationRuleValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValidationRuleValueType type) {
        this.valueType = type;
    }

    public DynamicFormValidatorInfoDTO(DynamicFormValidationRule rule, Boolean valueRequired, ValidationRuleValueType varType) {
        this.rule = rule;
        this.valueRequired = valueRequired;
        this.valueType = varType;
    }

    public DynamicFormValidatorInfoDTO() {
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
