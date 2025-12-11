package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serial;
import java.io.Serializable;

public class ChangeSeatPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
