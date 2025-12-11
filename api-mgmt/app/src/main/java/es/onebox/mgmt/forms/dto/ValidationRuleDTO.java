package es.onebox.mgmt.forms.dto;

import es.onebox.mgmt.forms.enums.ValidationRuleType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ValidationRuleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4601617869759834895L;

    private ValidationRuleType rule;
    private String value;

    public ValidationRuleType getRule() {
        return rule;
    }

    public void setRule(ValidationRuleType rule) {
        this.rule = rule;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
