package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record RecoverForgotPasswordRequestDTO (@NotNull String token, @JsonProperty("new_password") @NotNull String newPassword) implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;
}

