package es.onebox.common.datasources.distribution.dto.order.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public record Voucher (Long id,
                       String name,
                       String code,
                       @JsonProperty("redeemed_amount")
                       Double redeemedAmount,
                       Double balance,
                       ZonedDateTime expiration,
                       VoucherTexts texts
) implements Serializable {
    @Serial
    private static final long serialVersionUID = -6313967287077253728L;
}