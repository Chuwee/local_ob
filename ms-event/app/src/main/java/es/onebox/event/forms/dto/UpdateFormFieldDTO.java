package es.onebox.event.forms.dto;

import java.io.Serializable;
import java.util.List;

public class UpdateFormFieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private Boolean required;
    private Boolean visible;
    private Boolean uneditable;
    private Integer size;
    private List<ValidationRuleDTO> rules;
    private CustomerTypesDTO customerTypes;

    public List<ValidationRuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<ValidationRuleDTO> rules) {
        this.rules = rules;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CustomerTypesDTO getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(CustomerTypesDTO customerTypes) {
        this.customerTypes = customerTypes;
    }
} 