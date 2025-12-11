package es.onebox.mgmt.users.dto;

import java.io.Serial;
import java.io.Serializable;

public record ForgotPwdResponseDTO(String email) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
