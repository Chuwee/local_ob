package es.onebox.mgmt.channels.forms.dto;

import es.onebox.mgmt.channels.forms.enums.DynamicFormValidationRule;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
