package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.catalog.dto.ChangeSeatAmountType;

import java.io.Serializable;

public class ChangeSeatPrice implements Serializable {

    private ChangeSeatAmountType type;
    private ChangeSeatRefund refund;

    public ChangeSeatAmountType getType() {
        return type;
    }

    public void setType(ChangeSeatAmountType type) {
        this.type = type;
    }

    public ChangeSeatRefund getRefund() {
        return refund;
    }

    public void setRefund(ChangeSeatRefund changeSeatRefund) {
        this.refund = changeSeatRefund;
    }
}
