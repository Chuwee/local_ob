package es.onebox.common.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DynamicFormValidator {

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

    public DynamicFormValidator(String value) {
        this.value = value;
    }

    public DynamicFormValidator() {
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
