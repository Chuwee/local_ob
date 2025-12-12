package es.onebox.common.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AuthConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4444891757795953271L;

    private Boolean enabled;
    private List<AuthenticationMethodDTO> authenticationMethods;
    private List<Long> allowedCustomerTypes;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<AuthenticationMethodDTO> getAuthenticationMethods() {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(List<AuthenticationMethodDTO> authenticationMethods) {
        this.authenticationMethods = authenticationMethods;
    }

    public List<Long> getAllowedCustomerTypes() {
        return allowedCustomerTypes;
    }

    public void setAllowedCustomerTypes(List<Long> allowedCustomerTypes) {
        this.allowedCustomerTypes = allowedCustomerTypes;
    }
}
