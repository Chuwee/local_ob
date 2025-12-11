package es.onebox.event.forms.dto;

import es.onebox.event.forms.enums.ValidationRuleTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ValidationRuleDTO {

    private ValidationRuleTypeDTO rule;
    private String value;

    public ValidationRuleTypeDTO getRule() {
        return rule;
    }

    public void setRule(ValidationRuleTypeDTO rule) {
        this.rule = rule;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValidationRuleDTO(String value) {
        this.value = value;
    }

    public ValidationRuleDTO() {
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
} 