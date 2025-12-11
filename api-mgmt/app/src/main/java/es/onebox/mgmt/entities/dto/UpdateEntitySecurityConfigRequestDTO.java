package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEntitySecurityConfigRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6103461400747456192L;

    @NotNull
    @JsonProperty("password_config")
    private PasswordConfigDTO passwordConfig;

    public PasswordConfigDTO getPasswordConfig() {
        return passwordConfig;
    }

    public void setPasswordConfig(PasswordConfigDTO passwordConfig) {
        this.passwordConfig = passwordConfig;
    }
}
