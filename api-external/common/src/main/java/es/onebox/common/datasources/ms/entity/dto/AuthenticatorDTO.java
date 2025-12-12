package es.onebox.common.datasources.ms.entity.dto;

import es.onebox.common.datasources.ms.entity.enums.AuthenticatorTypeDTO;

import java.io.Serial;
import java.io.Serializable;

public class AuthenticatorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 10271963361598983L;

    private AuthenticatorTypeDTO type;
    private String id;

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
}

