package es.onebox.mgmt.datasources.common.dto;

import es.onebox.mgmt.datasources.common.enums.AuthenticationType;

import java.util.List;

public class AuthenticationMethod {

    private AuthenticationType type;
    private List<Authenticator> authenticators;

    public AuthenticationType getType() {
        return type;
    }

    public void setType(AuthenticationType type) {
        this.type = type;
    }

    public List<Authenticator> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(List<Authenticator> authenticators) {
        this.authenticators = authenticators;
    }

}
