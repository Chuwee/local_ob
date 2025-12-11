package es.onebox.mgmt.salerequests.configuration.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SaleRequestAllowRefundDTO implements Serializable {

    private static final long serialVersionUID = 2379706995494820989L;

    @JsonProperty("allow_refund")
    private Boolean allowRefund;

    public Boolean getAllowRefund() {
        return allowRefund;
    }

    public void setAllowRefund(Boolean allowRefund) {
        this.allowRefund = allowRefund;
    }
}
