package es.onebox.mgmt.vouchers.dto;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record SendEmailVoucherDTO(
        @NotNull SendEmailVoucherType type,
        @Email(message = "from must be a well-formed email address") String email,
        String subject,
        String body,
        @LanguageIETF String language
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
