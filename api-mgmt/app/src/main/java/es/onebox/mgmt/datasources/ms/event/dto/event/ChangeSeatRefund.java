package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;

public class ChangeSeatRefund implements Serializable {

    private ChangeSeatRefundType type;
    private ChangeSeatVoucherExpiry voucherExpiry;

    public ChangeSeatRefundType getType() {
        return type;
    }

    public void setType(ChangeSeatRefundType type) {
        this.type = type;
    }

    public ChangeSeatVoucherExpiry getVoucherExpiry() {
        return voucherExpiry;
    }

    public void setVoucherExpiry(ChangeSeatVoucherExpiry voucherExpiry) {
        this.voucherExpiry = voucherExpiry;
    }
}
