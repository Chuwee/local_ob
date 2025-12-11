package es.onebox.mgmt.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.forms.enums.ValidationRuleType;
import es.onebox.mgmt.forms.enums.ValidationRuleValueType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FormValidatorInfoDTO {

    private ValidationRuleType rule;
    @JsonProperty("value_required")
    private Boolean valueRequired;
    @JsonProperty("value_type")
    private ValidationRuleValueType valueType;

    public ValidationRuleType getRule() {
        return rule;
    }

    public void setRule(ValidationRuleType rule) {
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

    public FormValidatorInfoDTO(ValidationRuleType rule, Boolean valueRequired, ValidationRuleValueType varType) {
        this.rule = rule;
        this.valueRequired = valueRequired;
        this.valueType = varType;
    }

    public FormValidatorInfoDTO() {
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
