package es.onebox.event.events.dto;

import es.onebox.event.catalog.dto.ChangeSeatAmountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChangeSeatPriceDTO {

    @NotNull
    private ChangeSeatAmountType type;

    @Valid
    private ChangeSeatRefundDTO refund;

    public ChangeSeatAmountType getType() {
        return type;
    }

    public void setType(ChangeSeatAmountType type) {
        this.type = type;
    }

    public ChangeSeatRefundDTO getRefund() {
        return refund;
    }

    public void setRefund(ChangeSeatRefundDTO refund) {
        this.refund = refund;
    }
}
