package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatRefundDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "type cannot be null")
    private RefundTypeDTO type;

    @Valid
    @JsonProperty("voucher_expiry")
    private ChangeSeatVoucherExpiryDTO voucherExpiry;

    public RefundTypeDTO getType() {
        return type;
    }

    public void setType(RefundTypeDTO type) {
        this.type = type;
    }

    public ChangeSeatVoucherExpiryDTO getVoucherExpiry() {
        return voucherExpiry;
    }

    public void setVoucherExpiry(ChangeSeatVoucherExpiryDTO voucherExpiry) {
        this.voucherExpiry = voucherExpiry;
    }
}
