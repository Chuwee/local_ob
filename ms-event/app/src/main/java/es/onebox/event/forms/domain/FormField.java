package es.onebox.event.forms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class FormField implements Serializable {

    private String key;
    private Boolean required = false;
    private Boolean visible = false; // TODO - this should be remove in the next version
    private Boolean uneditable = false;
    private Integer size = 6;
    private List<ValidationRule> validationRules;
    @JsonProperty("customer_types")
    private CustomerTypes customerTypes;
    private List<FormField> fields;

    public FormField() {}

    public FormField(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getUneditable() {
        return uneditable;
    }

    public void setUneditable(Boolean uneditable) {
        this.uneditable = uneditable;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<ValidationRule> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }

    public CustomerTypes getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypes customerTypes) {
        this.customerTypes = customerTypes;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }
} 