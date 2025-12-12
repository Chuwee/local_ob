package es.onebox.common.datasources.webhook.dto.fever;

import es.onebox.common.datasources.ms.channel.dto.DynamicFormValidationRule;

public class DynamicFormValidatorDTO {

    private DynamicFormValidationRule rule;
    private String value;

    public DynamicFormValidationRule getRule() {
        return rule;
    }

    public void setRule(DynamicFormValidationRule rule) {
        this.rule = rule;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DynamicFormValidatorDTO(String value) {
        this.value = value;
    }

    public DynamicFormValidatorDTO() {
    }
}
