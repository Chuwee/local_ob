package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.channel.dto.DynamicFormValidationRule;

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
}
