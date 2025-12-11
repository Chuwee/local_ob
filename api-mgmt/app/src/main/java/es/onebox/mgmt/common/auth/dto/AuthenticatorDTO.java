package es.onebox.mgmt.common.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.auth.enums.AuthenticatorTypeDTO;
import es.onebox.mgmt.common.auth.enums.CustomerCreationDTO;

import java.util.Map;

public class AuthenticatorDTO {

    private AuthenticatorTypeDTO type;
    private String id;
    @JsonProperty("customer_creation")
    private CustomerCreationDTO customerCreation;
    private Map<String, String> properties;

    public AuthenticatorTypeDTO getType() {
        return type;
    }

    public void setType(AuthenticatorTypeDTO type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustomerCreationDTO getCustomerCreation() {
        return customerCreation;
    }

    public void setCustomerCreation(CustomerCreationDTO customerCreation) {
        this.customerCreation = customerCreation;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
