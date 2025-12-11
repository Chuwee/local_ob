package es.onebox.mgmt.common.auth.dto;

import es.onebox.mgmt.common.auth.enums.AuthenticationTypeDTO;

import java.util.List;

public class AuthenticationMethodDTO {

    private AuthenticationTypeDTO type;
    private List<AuthenticatorDTO> authenticators;

    public AuthenticationTypeDTO getType() {
        return type;
    }

    public void setType(AuthenticationTypeDTO type) {
        this.type = type;
    }

    public List<AuthenticatorDTO> getAuthenticators() {
        return authenticators;
    }

    public void setAuthenticators(List<AuthenticatorDTO> authenticatorDTOS) {
        this.authenticators = authenticatorDTOS;
    }

}
