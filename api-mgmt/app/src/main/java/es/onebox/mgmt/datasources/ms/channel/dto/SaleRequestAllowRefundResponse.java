package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;

public class SaleRequestAllowRefundResponse implements Serializable {

    private static final long serialVersionUID = 1908958138442392008L;

    private Boolean allowRefund;

    public Boolean getAllowRefund() {
        return allowRefund;
    }

    public void setAllowRefund(Boolean allowRefund) {
        this.allowRefund = allowRefund;
    }
}
