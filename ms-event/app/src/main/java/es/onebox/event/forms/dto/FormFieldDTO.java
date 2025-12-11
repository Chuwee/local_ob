package es.onebox.event.forms.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class FormFieldDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "key must not be empty")
    private String key;
    private Boolean required;
    private Boolean visible;
    private Boolean uneditable;
    private Integer size;
    private String type;
    private String validationType;
    private Boolean externalField;
    private List<ValidationRuleDTO> rules;
    private List<FormFieldValueDTO> values;
    private CustomerTypesDTO customerTypes;
    private List<FormFieldDTO> fields;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public Boolean getExternalField() {
        return externalField;
    }

    public void setExternalField(Boolean externalField) {
        this.externalField = externalField;
    }

    public List<ValidationRuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<ValidationRuleDTO> rules) {
        this.rules = rules;
    }

    public List<FormFieldValueDTO> getValues() {
        return values;
    }

    public void setValues(List<FormFieldValueDTO> values) {
        this.values = values;
    }

    public CustomerTypesDTO getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypesDTO customerTypes) {
        this.customerTypes = customerTypes;
    }

    public List<FormFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<FormFieldDTO> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

} 