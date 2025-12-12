package es.onebox.fever.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;


public final class CustomerVerifyRequestDTO extends CustomerPhoneVerificationRequestDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "code must not be null")
    private String code;

    public CustomerVerifyRequestDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
