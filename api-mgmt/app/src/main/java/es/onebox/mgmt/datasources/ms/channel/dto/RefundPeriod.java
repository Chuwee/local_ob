package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;

public class RefundPeriod implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean allowed;
    private Integer timeAfterPurchaseInMinutes;

    public Boolean getAllowed() {
        return allowed;
    }

    public void setAllowed(Boolean allowed) {
        this.allowed = allowed;
    }

    public Integer getTimeAfterPurchaseInMinutes() {
        return timeAfterPurchaseInMinutes;
    }

    public void setTimeAfterPurchaseInMinutes(Integer timeAfterPurchaseInMinutes) {
        this.timeAfterPurchaseInMinutes = timeAfterPurchaseInMinutes;
    }
}
