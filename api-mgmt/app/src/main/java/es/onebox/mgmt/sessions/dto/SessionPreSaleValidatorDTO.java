package es.onebox.mgmt.sessions.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.PresaleValidationMethod;

import java.io.Serializable;

public class SessionPreSaleValidatorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    @JsonProperty("validation_method")
    private PresaleValidationMethod validationMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PresaleValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(PresaleValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }
}
