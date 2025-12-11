package es.onebox.mgmt.users.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

public record MFARequestDTO(
        @NotBlank(message = "password must not be empty")
        String password,
        @Valid
        MFA mfa
)implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
