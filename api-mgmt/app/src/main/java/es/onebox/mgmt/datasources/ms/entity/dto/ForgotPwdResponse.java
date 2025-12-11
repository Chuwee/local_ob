package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public record ForgotPwdResponse(String email) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
