package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class FormField implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "key must not be empty")
    private String key;
    private Boolean required;
    private Boolean visible;
    private Boolean uneditable;
    private Boolean unique;
    private Boolean externalField;
    private String type;
    private Integer size;
    private List<ValidationRule> rules;
    private CustomerTypesField customerTypes;

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

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getExternalField() {
        return externalField;
    }

    public void setExternalField(Boolean externalField) {
        this.externalField = externalField;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<ValidationRule> getRules() {
        return rules;
    }

    public void setRules(List<ValidationRule> rules) {
        this.rules = rules;
    }

    public CustomerTypesField getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypesField customerTypes) {
        this.customerTypes = customerTypes;
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
