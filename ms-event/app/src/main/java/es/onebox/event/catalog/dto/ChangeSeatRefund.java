package es.onebox.event.catalog.dto;

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

    public void setVoucherExpiry(ChangeSeatVoucherExpiry changeSeatVoucherExpiry) {
        this.voucherExpiry = changeSeatVoucherExpiry;
    }
}
