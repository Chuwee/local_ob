package es.onebox.event.forms.domain;

import java.io.Serializable;

public class ValidationRule implements Serializable {

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
} 