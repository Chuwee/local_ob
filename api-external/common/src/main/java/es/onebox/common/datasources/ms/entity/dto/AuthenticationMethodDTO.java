package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.datasources.ms.entity.enums.AuthenticationTypeDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AuthenticationMethodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9129704457832839321L;

    private String id;
    private AuthenticationTypeDTO type;
    private List<AuthenticatorDTO> authenticators;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
