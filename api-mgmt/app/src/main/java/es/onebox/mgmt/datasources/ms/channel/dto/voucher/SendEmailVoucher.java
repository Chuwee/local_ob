package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record SendEmailVoucher(
        @NotNull SendEmailVoucherType type,
        String email,
        String subject,
        String body,
        String language
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
