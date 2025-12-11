package es.onebox.mgmt.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypesDTO;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class FormFieldDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "key must not be empty")
    private String key;
    private Boolean mandatory;
    private Boolean uneditable;
    private Boolean visible;
    private Boolean unique;
    @JsonProperty("external_field")
    private Boolean externalField;
    private String type;
    private Integer size;
    @JsonProperty("available_rules")
    private List<FormValidatorInfoDTO> availableRules;
    @JsonProperty("applied_rules")
    private List<ValidationRuleDTO> appliedRules;
    @JsonProperty("customer_types")
    private CustomerTypesDTO customerTypes;

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

    public List<FormValidatorInfoDTO> getAvailableRules() {
        return availableRules;
    }

    public void setAvailableRules(List<FormValidatorInfoDTO> availableRules) {
        this.availableRules = availableRules;
    }

    public List<ValidationRuleDTO> getAppliedRules() {
        return appliedRules;
    }

    public void setAppliedRules(List<ValidationRuleDTO> appliedRules) {
        this.appliedRules = appliedRules;
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

    public CustomerTypesDTO getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypesDTO customerTypes) {
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
