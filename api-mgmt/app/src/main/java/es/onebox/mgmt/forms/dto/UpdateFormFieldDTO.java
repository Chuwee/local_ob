package es.onebox.mgmt.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateFormFieldDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "key must not be empty")
    private String key;
    private Boolean mandatory;
    private Boolean uneditable;
    private Boolean visible;
    private Boolean unique;
    private Integer size;
    private List<ValidationRuleDTO> rules;
    @JsonProperty("customer_types")
    private CustomerTypesFieldDTO customerTypes;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getUneditable() {
        return uneditable;
    }

    public void setUneditable(Boolean uneditable) {
        this.uneditable = uneditable;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<ValidationRuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<ValidationRuleDTO> rules) {
        this.rules = rules;
    }

    public CustomerTypesFieldDTO getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypesFieldDTO customerTypes) {
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
