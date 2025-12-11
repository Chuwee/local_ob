package es.onebox.event.events.dto;

import es.onebox.event.catalog.dto.ChangeSeatRefundType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChangeSeatRefundDTO {

    @NotNull
    private ChangeSeatRefundType type;

    @Valid
    private ChangeSeatVoucherExpiryDTO voucherExpiry;

    public ChangeSeatRefundType getType() {
        return type;
    }

    public void setType(ChangeSeatRefundType type) {
        this.type = type;
    }

    public ChangeSeatVoucherExpiryDTO getVoucherExpiry() {
        return voucherExpiry;
    }

    public void setVoucherExpiry(ChangeSeatVoucherExpiryDTO voucherExpiry) {
        this.voucherExpiry = voucherExpiry;
    }
}
