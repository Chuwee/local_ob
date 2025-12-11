package es.onebox.mgmt.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record ForgotPwdRequestDTO(@NotNull @Email String email) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}

