package es.onebox.mgmt.users.dto;

import es.onebox.mgmt.users.enums.MFAType;
import jakarta.validation.constraints.NotNull;


import java.io.Serial;
import java.io.Serializable;

public class MFA implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type must not be empty")
    private MFAType type;
    private String code;

    public MFAType getType() {
        return type;
    }

    public void setType(MFAType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
