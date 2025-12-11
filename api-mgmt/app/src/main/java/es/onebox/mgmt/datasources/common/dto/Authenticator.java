package es.onebox.mgmt.datasources.common.dto;

import es.onebox.mgmt.datasources.common.enums.AuthenticatorType;
import es.onebox.mgmt.datasources.common.enums.CustomerCreation;

import java.util.Map;

public class Authenticator {

    private AuthenticatorType type;
    private String id;
    private CustomerCreation customerCreation;
    private Map<String, String> properties;

    public AuthenticatorType getType() {
        return type;
    }

    public void setType(AuthenticatorType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CustomerCreation getCustomerCreation() {
        return customerCreation;
    }

    public void setCustomerCreation(CustomerCreation customerCreation) {
        this.customerCreation = customerCreation;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
