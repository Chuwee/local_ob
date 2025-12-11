package es.onebox.mgmt.users.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;


public record ChangePwdRequestDTO (@NotNull String password, String token)
        implements Serializable {
    public ChangePwdRequestDTO(String password) {
        this(password, null);
    }

    @Serial
    private static final long serialVersionUID = 2L;
}