package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record ChangeSelfPwdRequestDTO (@JsonProperty("old_password") @NotNull String oldPassword,
                                       @NotNull String password)
        implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
